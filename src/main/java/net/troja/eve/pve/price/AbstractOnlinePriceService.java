package net.troja.eve.pve.price;

import net.troja.eve.pve.db.price.PriceBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractOnlinePriceService<T> {
    private static final Logger LOGGER = LogManager.getLogger(AbstractOnlinePriceService.class);

    protected RestTemplate restTemplate = new RestTemplate();

    public List<PriceBean> getPrices(final Collection<Integer> prices) {
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
            LOGGER.warn("Could not get prices for {}", values);
            return new ArrayList<>();
        }
    }

    protected HttpEntity<String> getHttpEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent", "PvE tracker");
        return new HttpEntity<>("", headers);
    }

    protected abstract ResponseEntity<T> queryPrices(String values);

    protected abstract List<PriceBean> transform(final T prices);

    protected String getTypeValuesString(final Collection<Integer> prices) {
        return prices.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    protected void setRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
