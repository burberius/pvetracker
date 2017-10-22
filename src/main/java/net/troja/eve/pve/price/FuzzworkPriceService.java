package net.troja.eve.pve.price;

/*
 * ====================================================
 * Eve Online PvE Tracker
 * ----------------------------------------------------
 * Copyright (C) 2017 Jens Oberender <j.obi@troja.net>
 * ----------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ====================================================
 */

import java.util.ArrayList;
import java.util.Collection;
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

    public List<PriceBean> getPrices(final Collection<Integer> prices) {
        final String values = prices.stream().map((final Integer value) -> value.toString()).collect(Collectors.joining(","));

        final ParameterizedTypeReference<Map<Integer, FuzzworkPriceBean>> responseType = 
                new ParameterizedTypeReference<Map<Integer, FuzzworkPriceBean>>() {
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
