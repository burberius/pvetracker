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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.db.price.PriceRepository;
import net.troja.eve.pve.price.fuzzwork.FuzzworkPriceService;

public class PriceServiceTest {
    private static final List<Integer> TYPE_IDS = Arrays.asList(34, 35);

    private final PriceService classToTest = new PriceService();

    @Mock
    private FuzzworkPriceService fuzzworkPriceService;
    @Mock
    private PriceRepository priceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setFuzzworkPriceService(fuzzworkPriceService);
        classToTest.setPriceRepository(priceRepository);
    }

    @Test
    public void getPrices() {
        final List<PriceBean> dbPrices = Arrays.asList(new PriceBean(34, 5.123));
        when(priceRepository.findAllById(TYPE_IDS)).thenReturn(dbPrices);
        final List<PriceBean> restPrices = Arrays.asList(new PriceBean(35, 6.2123));
        when(fuzzworkPriceService.getPrices(Arrays.asList(35))).thenReturn(restPrices);

        final Map<Integer, Double> prices = classToTest.getPrices(TYPE_IDS);

        assertThat(prices.size(), equalTo(2));
        verify(priceRepository).saveAll(restPrices);
    }

    @Test
    public void getPricesNoDownload() {
        final List<PriceBean> dbPrices = Arrays.asList(new PriceBean(34, 5.123), new PriceBean(35, 6.2123));
        when(priceRepository.findAllById(TYPE_IDS)).thenReturn(dbPrices);

        final Map<Integer, Double> prices = classToTest.getPrices(TYPE_IDS);

        verifyNoMoreInteractions(fuzzworkPriceService);
        assertThat(prices.size(), equalTo(2));
    }
}
