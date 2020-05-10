package net.troja.eve.pve.esi;

import net.troja.eve.esi.ApiCallback;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.TypeResponse;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class NamesUpdateService {
    private static final Logger LOGGER = LogManager.getLogger(NamesUpdateService.class);
    private static final String[] LANGUAGES = {"de", "en", "fr", "ru", "ja"};
    private static final int BATCH_SIZE = 100;
    private static final String QUERY_SAVE = "INSERT INTO type_translation_new(type_id, language, name) values (?, ?, ?)";

    @Autowired
    private TypeTranslationRepository typeTranslationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UniverseApi universeApi = new UniverseApi();
    private List<Integer> typeIds = new ArrayList<>();
    private Queue<TypeTranslationBean> typesQueue = new ConcurrentLinkedQueue<>();
    private int typesPage = 1;
    private long start;

    public void update() {
        start = System.currentTimeMillis();
        jdbcTemplate.execute("CREATE TABLE type_translation_new LIKE type_translation");
        try {
            universeApi.getUniverseTypesAsync(GeneralEsiService.DATASOURCE, null, typesPage, getApiCallback());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void updateNames() {
        typeIds.stream().forEach(t -> updateName(t));
        System.out.println(typeIds.size() + " " + (System.currentTimeMillis() - start));
    }

    @Async
    private void updateName(int typeId) {
        try {
            TypeResponse response = universeApi.getUniverseTypesTypeId(typeId, null, GeneralEsiService.DATASOURCE, null, null);
            System.out.println("TypeId: " + typeId + " " + response.getName());
            typesQueue.add(new TypeTranslationBean(typeId, "en", response.getName()));
            if(typesQueue.size() >= BATCH_SIZE) {
                writeToDb();
            }
        } catch (ApiException e) {
            System.out.println(e.getCode() + " " + e.getMessage());
        }

    }

    private void writeToDb() {
        LOGGER.info("Writing batch to DB");
        List<TypeTranslationBean> batch = new ArrayList<>();
        int max = Math.min(BATCH_SIZE, typesQueue.size());
        for (int pos = 0; pos < max; pos++) {
            batch.add(typesQueue.poll());
        }
        jdbcTemplate.batchUpdate(QUERY_SAVE, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                TypeTranslationBean translationBean = batch.get(i);
                ps.setInt(1, translationBean.getTypeId());
                ps.setString(2, translationBean.getLanguage());
                ps.setString(3, translationBean.getName());
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
    }

    private ApiCallback<List<Integer>> getApiCallback() {
        System.out.println("Api Callback");
        return new ApiCallback<>() {
            @Override
            public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                LOGGER.info("error: " + statusCode + " " + e.getMessage());
            }

            @Override
            public void onSuccess(List<Integer> result, int statusCode, Map<String, List<String>> responseHeaders) {
                System.out.println("Success");
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

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
