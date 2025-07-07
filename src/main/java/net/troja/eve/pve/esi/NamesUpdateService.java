package net.troja.eve.pve.esi;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.api.StatusApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.StatusResponse;
import net.troja.eve.esi.model.TypeResponse;
import net.troja.eve.pve.db.type.Language;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import net.troja.eve.pve.discord.DiscordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static net.troja.eve.pve.ApiUtils.getETag;
import static net.troja.eve.pve.ApiUtils.getPagesMax;

@Service
@RequiredArgsConstructor
public class NamesUpdateService {
    private static final Logger LOGGER = LogManager.getLogger(NamesUpdateService.class);
    private static final String STORAGE_LANGUAGE = "xx";
    private static final int STORAGE_ID = 0;

    private final TypeTranslationRepository typeTranslationRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final DiscordService discordService;
    @Value("${namesupdate}")
    private boolean namesUpdateActive;

    private boolean initialized = false;
    private long start;
    private String lastApiVersion;
    private final UniverseApi universeApi = new UniverseApi();
    private final StatusApi statusApi = new StatusApi();
    private final List<Integer> typeIds = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicInteger unchangedCount = new AtomicInteger();
    private final AtomicInteger updateCount = new AtomicInteger();
    private BlockingQueue<Runnable> taskQueue;

    @PostConstruct
    public void init() {
        Optional<TypeTranslationBean> storage =
                typeTranslationRepository.findByTypeIdAndLanguage(STORAGE_ID, STORAGE_LANGUAGE);
        storage.ifPresent(typeTranslationBean -> lastApiVersion = typeTranslationBean.getName());
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
        Optional<Boolean> status;
        do {
            status = checkApiDifferentVersion();
            if (status.isPresent() && !status.get()) {
                LOGGER.info("Version didn't change");
                discordService.sendMessage("API Version didn't change");
                return;
            } else if (status.isEmpty()) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    LOGGER.error("Could not sleep");
                    Thread.currentThread().interrupt();
                }
            }
        } while (status.isEmpty());

        if(!namesUpdateActive) {
            LOGGER.info("Names update disabled!");
            return;
        }

        discordService.sendMessage("Starting names update");
        reset();
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

    private void reset() {
        typeIds.clear();
        counter.set(0);
        updateCount.set(0);
        unchangedCount.set(0);
        start = System.currentTimeMillis();
    }

    private Optional<Boolean> checkApiDifferentVersion() {
        try {
            StatusResponse status = statusApi.getStatus(GeneralEsiService.DATASOURCE, null);
            LOGGER.info("Current Api Version: {}", status.getServerVersion());
            return Optional.of(!status.getServerVersion().equals(lastApiVersion));
        } catch (ApiException e) {
            LOGGER.error("Could not get API Status", e);
            return Optional.empty();
        }
    }

    private void updateNames() {
        for (Integer typeId : typeIds) {
            while (taskExecutor.getActiveCount() == taskExecutor.getMaxPoolSize() && taskQueue.remainingCapacity() < 500) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    LOGGER.error("Could not sleep");
                    Thread.currentThread().interrupt();
                }
            }
            taskExecutor.execute(() -> updateName(typeId));
        }
    }

    private void updateName(int typeId) {
        List<TypeTranslationBean> existing = typeTranslationRepository.findByTypeId(typeId);
        for (Language language : Language.values()) {
            String lang = language.name().toLowerCase(Locale.ROOT);
            Optional<TypeTranslationBean> entry = existing.stream().filter(x -> lang.equals(x.getLanguage())).findFirst();
            String etag = entry.map(TypeTranslationBean::getEtag).orElse(null);
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
            discordService.sendMessage("Updated " + count + " entries after " + time + " (" + updateCount.get() + "/" + unchangedCount.get() + ")");
            typeIds.clear();
            updateApiVersion();
        } else if (count % 100 == 0) {
            long timeDiff = System.currentTimeMillis() - start;
            String rest = calcRest(count, timeDiff);
            LOGGER.info("Finished entry {} rest: {} - {}/{}",
                    count, rest, updateCount.get(), unchangedCount.get());
        }
    }

    private String calcRest(int count, long timeDiff) {
        double timePer = (double) timeDiff / count;
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
}
