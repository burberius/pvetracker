package net.troja.eve.pve.price;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.troja.eve.pve.db.price.PriceBean;

@Service
public class FuzzworkPriceService {
    private static final Logger LOGGER = LogManager.getLogger(FuzzworkPriceService.class);
    private static final String ADDRESS = "https://market.fuzzwork.co.uk/aggregates/?region=10000002&types=";

    @Autowired
    private RestTemplate restTemplate;

    public FuzzworkPriceService() {
        super();
    }

    public List<PriceBean> getPrices(final List<Integer> prices) {
        final String values = prices.stream().map((final Integer value) -> value.toString()).collect(Collectors.joining(","));

        final ParameterizedTypeReference<Map<Integer, FuzzworkPriceBean>> responseType = new ParameterizedTypeReference<Map<Integer, FuzzworkPriceBean>>() {
        };
        final HttpEntity<String> requestEntity = new HttpEntity<>("");
        final ResponseEntity<Map<Integer, FuzzworkPriceBean>> response = restTemplate.exchange(ADDRESS + values, HttpMethod.GET, requestEntity,
                responseType);
        if (response.getStatusCode() == HttpStatus.OK) {
            final Map<Integer, FuzzworkPriceBean> result = response.getBody();
            return transform(result);
        } else {
            LOGGER.warn("Could not get prices for {}: {}", values, response.getStatusCode());
            return new ArrayList<>();
        }
    }

    private static List<PriceBean> transform(final Map<Integer, FuzzworkPriceBean> prices) {
        final List<PriceBean> result = new ArrayList<>(prices.size());
        for (final Entry<Integer, FuzzworkPriceBean> entry : prices.entrySet()) {
            result.add(new PriceBean(entry.getKey(), entry.getValue().getSell().getPercentile()));
        }
        return result;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
