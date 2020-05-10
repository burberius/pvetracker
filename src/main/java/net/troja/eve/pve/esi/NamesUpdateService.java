package net.troja.eve.pve.esi;

import groovy.time.TimeDuration;
import net.troja.eve.esi.ApiCallback;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.StatusApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.StatusResponse;
import net.troja.eve.esi.model.TypeResponse;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class NamesUpdateService {
    private static final Logger LOGGER = LogManager.getLogger(NamesUpdateService.class);
    private static final String STORAGE_LANGUAGE = "xx";
    private static final int STORAGE_ID = 0;
    private static final String[] LANGUAGES = {"de", "en", "fr", "ru", "ja"};

    @Autowired
    private TypeTranslationRepository typeTranslationRepository;

    private UniverseApi universeApi = new UniverseApi();
    private StatusApi statusApi = new StatusApi();
    private List<Integer> typeIds = new ArrayList<>();
    private int typesPage;
    private long start;
    private AtomicInteger counter = new AtomicInteger();
    private String lastApiVersion;

    @PostConstruct
    public void init() {
        Optional<TypeTranslationBean> storage =
                typeTranslationRepository.findByTypeIdAndLanguage(STORAGE_ID, STORAGE_LANGUAGE);
        if(storage.isPresent()) {
            lastApiVersion = storage.get().getName();
        }
        LOGGER.info("Last Api Version: {}", lastApiVersion);

        update();
    }

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
        LOGGER.info("Starting name update");
        counter.set(0);
        typesPage = 1;
        typeIds.clear();
        start = System.currentTimeMillis();
        try {
            universeApi.getUniverseTypesAsync(GeneralEsiService.DATASOURCE, null, typesPage, getApiCallback());
        } catch (ApiException e) {
            e.printStackTrace();
        }
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
        typeIds.stream().forEach(t -> updateName(t));
        LOGGER.info("Started all threads for {} typeIds after {} ms", typeIds.size(), (System.currentTimeMillis() - start));
    }

    @Async
    private void updateName(int typeId) {
        try {
            String lang = "en";
            TypeResponse response = universeApi.getUniverseTypesTypeId(typeId, lang.equals("en") ? "en-us" : lang,
                    GeneralEsiService.DATASOURCE, null, lang.equals("en") ? "en-us" : lang);
            TypeTranslationBean translationBean = new TypeTranslationBean(typeId, lang, response.getName());
            Optional<TypeTranslationBean> result = typeTranslationRepository.findByTypeIdAndLanguage(typeId, lang);
            if(!result.isPresent()) {
                typeTranslationRepository.save(translationBean);
            } else if (!translationBean.equals(result.get())) {
                TypeTranslationBean dbBean = result.get();
                dbBean.setName(translationBean.getName());
                typeTranslationRepository.save(dbBean);
            }
            int count = counter.incrementAndGet();
            if(count == typeIds.size()) {
                LOGGER.info("Finished all entries after {} ms", count, System.currentTimeMillis() - start );
                updateApiVersion();
            } else if( count % 100 == 0) {
                long timeDiff = System.currentTimeMillis() - start;
                LOGGER.info("Finished entry {} after {} ms, rest: {}",
                        count, timeDiff, calcRest(count, timeDiff));
            }
        } catch (ApiException e) {
            LOGGER.error("TypeId: {} Code: {}", typeId, e.getCode());
            updateName(typeId);
        }
    }

    private String calcRest(int count, long timeDiff) {
        double timePer = timeDiff / count;
        int rest = typeIds.size() - count;
        Duration timeLeft = Duration.ofMillis(Math.round(timePer * rest));
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

    private ApiCallback<List<Integer>> getApiCallback() {
        return new ApiCallback<>() {
            @Override
            public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                LOGGER.info("error: " + statusCode + " " + e.getMessage());
            }

            @Override
            public void onSuccess(List<Integer> result, int statusCode, Map<String, List<String>> responseHeaders) {
                typeIds.addAll(result);
                int maxPages = Integer.valueOf(responseHeaders.get("X-Pages").get(0));
                if(typesPage < maxPages) {
                    typesPage++;
                    try {
                        universeApi.getUniverseTypesAsync(GeneralEsiService.DATASOURCE, null, typesPage, getApiCallback());
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    updateNames();
                }
            }

            @Override
            public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

            }

            @Override
            public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

            }
        };
    }

    public void setTypeTranslationRepository(TypeTranslationRepository typeTranslationRepository) {
        this.typeTranslationRepository = typeTranslationRepository;
    }
}
