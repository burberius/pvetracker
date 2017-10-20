package net.troja.eve.pve.price;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final RestTemplate restTemplate = new RestTemplate();

    public List<PriceBean> getPrices(final List<Integer> prices) {
        final String values = prices.stream().map((final Integer value) -> value.toString()).collect(Collectors.joining(","));

        final ParameterizedTypeReference<Map<Integer, FuzzworkPrice>> responseType = new ParameterizedTypeReference<Map<Integer, FuzzworkPrice>>() {
        };
        final HttpEntity<String> requestEntity = new HttpEntity<>("");
        final ResponseEntity<Map<Integer, FuzzworkPrice>> response = restTemplate.exchange(ADDRESS + values, HttpMethod.GET, requestEntity,
                responseType);
        if (response.getStatusCode() == HttpStatus.OK) {
            final Map<Integer, FuzzworkPrice> result = response.getBody();
            return transform(result);
        } else {
            LOGGER.warn("Could not get prices for {}: {}", values, response.getStatusCode());
            return new ArrayList<>();
        }
    }

    private List<PriceBean> transform(final Map<Integer, FuzzworkPrice> prices) {
        final List<PriceBean> result = new ArrayList<>();
        for (final Entry<Integer, FuzzworkPrice> entry : prices.entrySet()) {
            result.add(new PriceBean(entry.getKey(), entry.getValue().getSell().getPercentile()));
        }
        return result;
    }
}
