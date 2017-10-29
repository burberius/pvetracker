package net.troja.eve.pve.price.fuzzwork;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.price.AbstractOnlinePriceService;

@Service
public class FuzzworkPriceService extends AbstractOnlinePriceService<Map<Integer, FuzzworkPriceBean>> {
    private static final String ADDRESS = "https://market.fuzzwork.co.uk/aggregates/?station=60003760&types=";

    public FuzzworkPriceService() {
        super();
    }

    @Override
    protected ResponseEntity<Map<Integer, FuzzworkPriceBean>> queryPrices(final String values) {
        final ParameterizedTypeReference<Map<Integer, FuzzworkPriceBean>> responseType = 
                new ParameterizedTypeReference<Map<Integer, FuzzworkPriceBean>>() {
        };
        return restTemplate.exchange(ADDRESS + values, HttpMethod.GET, getHttpEntity(), responseType);
    }

    @Override
    protected List<PriceBean> transform(final Map<Integer, FuzzworkPriceBean> prices) {
        final List<PriceBean> result = new ArrayList<>(prices.size());
        for (final Entry<Integer, FuzzworkPriceBean> entry : prices.entrySet()) {
            final double value = entry.getValue().getSell().getPercentile();
            if (value > 0) {
                result.add(new PriceBean(entry.getKey(), value));
            }
        }
        return result;
    }
}
