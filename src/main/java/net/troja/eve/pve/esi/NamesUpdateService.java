package net.troja.eve.pve.esi;

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
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private boolean initialized = false;
    private long start;
    private String lastApiVersion;
    private UniverseApi universeApi = new UniverseApi();
    private StatusApi statusApi = new StatusApi();
    private List<Integer> typeIds = new ArrayList<>();
    private AtomicInteger counter = new AtomicInteger();
    private AtomicInteger unchangedCount = new AtomicInteger();
    private AtomicInteger updateCount = new AtomicInteger();
    private BlockingQueue<Runnable> taskQueue;

    @PostConstruct
    public void init() {
        Optional<TypeTranslationBean> storage =
                typeTranslationRepository.findByTypeIdAndLanguage(STORAGE_ID, STORAGE_LANGUAGE);
        if (storage.isPresent()) {
            lastApiVersion = storage.get().getName();
        }
        LOGGER.info("Last Api Version: {}", lastApiVersion);

        taskQueue = taskExecutor.getThreadPoolExecutor().getQueue();
    }

    @Scheduled(fixedRate = 2000000, initialDelay = 5000)
    public void initialUpdate() {
        if (!initialized) {
            initialized = true;
            update();
        }
    }

    @Scheduled(cron = "0 10 11 * * ?")
    public void update() {
        if (!typeIds.isEmpty())
            return;
        LOGGER.info("Starting name update");
        Boolean status;
        do {
            status = checkApiDifferentVersion();
            if (status == Boolean.FALSE) {
                LOGGER.info("Version didn't change");
                return;
            } else if (status == null) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    LOGGER.error("Could not sleep");
                }
            }
        } while (status == null);
        typeIds.clear();
        start = System.currentTimeMillis();
        int typesPage = 1;
        int typesPagesMax = 0;
        while (typesPagesMax == 0 || typesPage <= typesPagesMax) {
            try {
                ApiResponse<List<Integer>> resp = universeApi.getUniverseTypesWithHttpInfo(GeneralEsiService.DATASOURCE, null, typesPage);
                typesPagesMax = getPagesMax(resp);
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
            while (taskExecutor.getActiveCount() == taskExecutor.getMaxPoolSize() && taskQueue.remainingCapacity() < 500) {
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
        List<TypeTranslationBean> existing = typeTranslationRepository.findByTypeId(typeId);
        for (Language language : Language.values()) {
            String lang = language.name();
            Optional<TypeTranslationBean> entry = existing.stream().filter(x -> lang.equals(x.getLanguage())).findFirst();
            String etag = entry.isPresent() ? entry.get().getEtag() : null;
            try {
                ApiResponse<TypeResponse> response = universeApi.getUniverseTypesTypeIdWithHttpInfo(typeId, lang.equals("en") ? "en-us" : lang,
                        GeneralEsiService.DATASOURCE, etag, lang.equals("en") ? "en-us" : lang);

                TypeResponse responseData = response.getData();

                TypeTranslationBean work = null;
                if (entry.isPresent()) {
                    work = entry.get();
                    work.setName(responseData.getName());
                    work.setEtag(getETag(response));
                } else {
                    work = new TypeTranslationBean(typeId, lang, responseData.getName(), getETag(response));
                }
                typeTranslationRepository.save(work);
                updateCount.incrementAndGet();
            } catch (ApiException e) {
                if (e.getCode() == HttpStatus.NOT_MODIFIED.value()) {
                    unchangedCount.incrementAndGet();
                    continue;
                }
                LOGGER.error("TypeId: {} Code: {}", typeId, e.getCode());
                updateName(typeId);
            }
        }
        int count = counter.incrementAndGet();
        if (count == typeIds.size()) {
            String time = getDurationString(System.currentTimeMillis() - start);
            LOGGER.info("Finished all {} entries after {} - {}/{}", count, time, updateCount.get(), unchangedCount.get());
            typeIds.clear();
            updateApiVersion();
        } else if (count % 100 == 0) {
            long timeDiff = System.currentTimeMillis() - start;
            LOGGER.info("Finished entry {} rest: {} - {}/{}",
                    count, calcRest(count, timeDiff), updateCount.get(), unchangedCount.get());
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
            TypeTranslationBean storageBean = new TypeTranslationBean(STORAGE_ID, STORAGE_LANGUAGE, lastApiVersion, null);
            if (storage.isPresent()) {
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

    public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    private String getETag(ApiResponse<TypeResponse> resp) {
        return resp.getHeaders().get("etag").get(0);
    }

    private Integer getPagesMax(ApiResponse<?> resp) {
        return Integer.valueOf(resp.getHeaders().get("X-Pages").get(0));
    }
}
