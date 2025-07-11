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

import lombok.RequiredArgsConstructor;
import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.db.price.PriceRepository;
import net.troja.eve.pve.price.fuzzwork.FuzzworkPriceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PriceService {
    private static final int PRICE_AGE_HOURS = 2;
    private static final int INITIAL_DELAY_10S = 10_000;
    private static final int DELETE_INTERVAL_1HOUR = 3_600_000;

    private static final Logger LOGGER = LogManager.getLogger(PriceService.class);

    private final FuzzworkPriceService fuzzworkPriceService;
    private final ContractPriceService contractPriceService;
    private final PriceRepository priceRepository;

    public Map<Integer, Double> getPrices(final List<Integer> prices) {
        LOGGER.info("getPrices: {}", prices);
        final Map<Integer, Double> result = new ConcurrentHashMap<>(prices.size());
        final Iterable<PriceBean> allById = priceRepository.findAllById(prices);
        final Set<Integer> rest = new HashSet<>(prices);
        for (final PriceBean price : allById) {
            result.put(price.getTypeId(), price.getValue());
            rest.remove(price.getTypeId());
        }
        if (!rest.isEmpty()) {
            LOGGER.info("retrieve prices from fuzzwork: {}", rest);
            final List<PriceBean> list = fuzzworkPriceService.getPrices(new ArrayList<>(rest));
            priceRepository.saveAll(list);
            for (final PriceBean price : list) {
                rest.remove(price.getTypeId());
                result.put(price.getTypeId(), price.getValue());
            }
        }
        if (!rest.isEmpty()) {
            LOGGER.info("retrieve prices from conract prices: {}", rest);
            for(int typeId : rest) {
                PriceBean price = contractPriceService.getPrice(typeId);
                if(price != null) {
                    priceRepository.save(price);
                    result.put(price.getTypeId(), price.getValue());
                }
            }
        }
        return result;
    }

    @Scheduled(fixedRate = DELETE_INTERVAL_1HOUR, initialDelay = INITIAL_DELAY_10S)
    public void deleteOld() {
        priceRepository.deleteByCreatedBefore(LocalDateTime.now(PvEApplication.DEFAULT_ZONE)
                .minusHours(PRICE_AGE_HOURS));
    }
}
