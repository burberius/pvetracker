package net.troja.eve.pve.price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import net.troja.eve.pve.db.price.PriceBean;

public abstract class AbstractOnlinePriceService<T> {
    private static final Logger LOGGER = LogManager.getLogger(AbstractOnlinePriceService.class);

    @Autowired
    protected RestTemplate restTemplate;

    public List<PriceBean> getPrices(final Collection<Integer> prices) {
        LOGGER.info("Get prices for {}", prices);
        final String values = getTypeValuesString(prices);

        ResponseEntity<T> response = null;
        try {
            response = queryPrices(values);
        } catch (final RestClientException e) {
            LOGGER.warn("Could not get data {}", e.getMessage(), e);
        }
        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            final T result = response.getBody();
            return transform(result);
        } else {
            LOGGER.warn("Could not get prices for {} - " + values, values);
            return new ArrayList<>();
        }
    }

    protected HttpEntity<String> getHttpEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent", "PvE today");
        final HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        return requestEntity;
    }

    protected abstract ResponseEntity<T> queryPrices(String values);

    protected abstract List<PriceBean> transform(final T prices);

    protected String getTypeValuesString(final Collection<Integer> prices) {
        return prices.stream().map((final Integer value) -> value.toString()).collect(Collectors.joining(","));
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    protected void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}