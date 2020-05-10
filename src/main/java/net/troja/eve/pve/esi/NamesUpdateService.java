package net.troja.eve.pve.esi;

import net.troja.eve.esi.ApiCallback;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.api.StatusApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.StatusResponse;
import net.troja.eve.esi.model.TypeResponse;
import net.troja.eve.pve.db.type.Language;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class NamesUpdateService {
    private static final Logger LOGGER = LogManager.getLogger(NamesUpdateService.class);
    private static final String STORAGE_LANGUAGE = "xx";
    private static final int STORAGE_ID = 0;

    @Autowired
    private TypeTranslationRepository typeTranslationRepository;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private UniverseApi universeApi = new UniverseApi();
    private StatusApi statusApi = new StatusApi();
    private List<Integer> typeIds = new ArrayList<>();
    private long start;
    private AtomicInteger counter = new AtomicInteger();
    private AtomicInteger existsCount = new AtomicInteger();
    private AtomicInteger updateCount = new AtomicInteger();
    private String lastApiVersion;
    private Map<Integer, String[]> dbContent = new HashMap<>();
    private BlockingQueue<Runnable> taskQueue;

    @PostConstruct
    public void init() {
        Optional<TypeTranslationBean> storage =
                typeTranslationRepository.findByTypeIdAndLanguage(STORAGE_ID, STORAGE_LANGUAGE);
        if(storage.isPresent()) {
            lastApiVersion = storage.get().getName();
        }
        LOGGER.info("Last Api Version: {}", lastApiVersion);

        taskQueue = taskExecutor.getThreadPoolExecutor().getQueue();

        update();
    }

    private void initDbContent() {
        Iterator<TypeTranslationBean> all = typeTranslationRepository.findAll().iterator();

        while(all.hasNext()) {
            TypeTranslationBean translationBean = all.next();
            if(translationBean.getLanguage().equals(STORAGE_LANGUAGE)) continue;
            String[] strings = dbContent.get(translationBean.getTypeId());
            if(strings == null) {
                strings = new String[Language.values().length];
                dbContent.put(translationBean.getTypeId(), strings);
            }
            strings[Language.valueOf(translationBean.getLanguage()).ordinal()] = translationBean.getName();
        }
    }

    @Scheduled(cron = "0 10 12 * * ?")
    public void update() {
        Boolean status;
        do {
            status = checkApiDifferentVersion();
            if(status == Boolean.FALSE) {
                return;
            } else if(status == null) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    LOGGER.error("Could not sleep");
                }
            }
        } while (status == null);
        initDbContent();
        counter.set(0);
        typeIds.clear();
        LOGGER.info("Starting name update");
        start = System.currentTimeMillis();
        int typesPage = 1;
        int typesPagesMax = 0;
        while(typesPagesMax == 0 || typesPage <= typesPagesMax) {
            try {
                ApiResponse<List<Integer>> resp = universeApi.getUniverseTypesWithHttpInfo(GeneralEsiService.DATASOURCE, null, typesPage);
                typesPagesMax = Integer.valueOf(resp.getHeaders().get("X-Pages").get(0));
                typeIds.addAll(resp.getData());
                typesPage++;
            } catch (ApiException e) {
                LOGGER.error("Could not get page {} of typeIds", typesPage);
            }
        }
        updateNames();
    }

    private Boolean checkApiDifferentVersion() {
        try {
            StatusResponse status = statusApi.getStatus(GeneralEsiService.DATASOURCE, null);
            LOGGER.info("Current Api Version: {}", status.getServerVersion());
            return !status.getServerVersion().equals(lastApiVersion);
        } catch (ApiException e) {
            LOGGER.error("Could not get API Status", e);
            return null;
        }
    }

    private void updateNames() {
        for (Integer typeId : typeIds) {
            while(taskExecutor.getActiveCount() == taskExecutor.getMaxPoolSize() && taskQueue.remainingCapacity() < 200 ) {
                LOGGER.info("Sleeping: {}", taskQueue.remainingCapacity());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    LOGGER.error("Could not sleep");
                }
            }
            taskExecutor.execute(() -> updateName(typeId));
        }
    }

    private void updateName(int typeId) {
        try {
            for(Language language : Language.values()) {
                String lang = language.name();
                TypeResponse response = universeApi.getUniverseTypesTypeId(typeId, lang.equals("en") ? "en-us" : lang,
                        GeneralEsiService.DATASOURCE, null, lang.equals("en") ? "en-us" : lang);
                String[] storedNames = dbContent.get(typeId);
                if(storedNames == null || storedNames[Language.valueOf(lang).ordinal()] == null) {
                    typeTranslationRepository.save(new TypeTranslationBean(typeId, lang, response.getName()));
                    updateCount.incrementAndGet();
                } else if(!storedNames[Language.valueOf(lang).ordinal()].equals(response.getName())) {
                    TypeTranslationBean translationBean = typeTranslationRepository.findByTypeIdAndLanguage(typeId, lang).get();
                    translationBean.setName(response.getName());
                    typeTranslationRepository.save(translationBean);
                    updateCount.incrementAndGet();
                } else {
                    existsCount.incrementAndGet();
                }
            }
            int count = counter.incrementAndGet();
            if(count == typeIds.size()) {
                String time = getDurationString(System.currentTimeMillis() - start);
                LOGGER.info("Finished all {} entries after {} ms - {}/{}", count, time, updateCount.get(), existsCount.get());
                updateApiVersion();
                dbContent.clear();
            } else if( count % 100 == 0) {
                long timeDiff = System.currentTimeMillis() - start;
                LOGGER.info("Finished entry {} rest: {} - {}/{}",
                        count, calcRest(count, timeDiff), updateCount.get(), existsCount.get());
            }
        } catch (ApiException e) {
            LOGGER.error("TypeId: {} Code: {}", typeId, e.getCode());
            updateName(typeId);
        }
    }

    private String calcRest(int count, long timeDiff) {
        double timePer = timeDiff / count;
        int rest = typeIds.size() - count;
        long left = Math.round(timePer * rest);
        return getDurationString(left);
    }

    private String getDurationString(long millis) {
        Duration timeLeft = Duration.ofMillis(millis);
        return timeLeft.toMinutes() + "m " + timeLeft.toSecondsPart() + "s";
    }

    private void updateApiVersion() {
        try {
            StatusResponse status = statusApi.getStatus(GeneralEsiService.DATASOURCE, null);
            lastApiVersion = status.getServerVersion();
            Optional<TypeTranslationBean> storage = typeTranslationRepository.findByTypeIdAndLanguage(STORAGE_ID, STORAGE_LANGUAGE);
            TypeTranslationBean storageBean = new TypeTranslationBean(STORAGE_ID, STORAGE_LANGUAGE, lastApiVersion);
            if(storage.isPresent()) {
                storageBean = storage.get();
                storageBean.setName(lastApiVersion);
            }
            typeTranslationRepository.save(storageBean);
            LOGGER.info("Saved new Api Version: {}", lastApiVersion);
        } catch (ApiException e) {
            LOGGER.error("Could not get API Status", e);
        }
    }

    public void setTypeTranslationRepository(TypeTranslationRepository typeTranslationRepository) {
        this.typeTranslationRepository = typeTranslationRepository;
    }
}
