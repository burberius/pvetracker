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
import java.util.List;
import java.util.Map;

import net.troja.eve.pve.price.contract.ContractPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.db.price.PriceRepository;
import net.troja.eve.pve.price.fuzzwork.FuzzworkPriceService;

public class PriceServiceTest {
    private static final List<Integer> TYPE_IDS = Arrays.asList(34, 35, 17716);

    private final PriceService classToTest = new PriceService();

    @Mock
    private FuzzworkPriceService fuzzworkPriceService;
    @Mock
    private ContractPriceService contractPriceService;
    @Mock
    private PriceRepository priceRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setFuzzworkPriceService(fuzzworkPriceService);
        classToTest.setContractPriceService(contractPriceService);
        classToTest.setPriceRepository(priceRepository);
    }

    @Test
    public void getPrices() {
        final List<PriceBean> dbPrices = Arrays.asList(new PriceBean(34, 5.123));
        when(priceRepository.findAllById(TYPE_IDS)).thenReturn(dbPrices);
        final List<PriceBean> restPrices = Arrays.asList(new PriceBean(35, 6.2123));
        when(fuzzworkPriceService.getPrices(Arrays.asList(35, 17716))).thenReturn(restPrices);
        PriceBean gila = new PriceBean(17716, 150_000_00);
        when(contractPriceService.getPrice(17716)).thenReturn(gila);

        final Map<Integer, Double> prices = classToTest.getPrices(TYPE_IDS);

        assertThat(prices.size(), equalTo(3));
        verify(priceRepository).saveAll(restPrices);
        verify(priceRepository).save(gila);
    }

    @Test
    public void getPricesNoDownload() {
        final List<PriceBean> dbPrices = Arrays.asList(new PriceBean(34, 5.123),
                new PriceBean(35, 6.2123), new PriceBean(17716, 150_000_00));
        when(priceRepository.findAllById(TYPE_IDS)).thenReturn(dbPrices);

        final Map<Integer, Double> prices = classToTest.getPrices(TYPE_IDS);

        verifyNoMoreInteractions(fuzzworkPriceService);
        assertThat(prices.size(), equalTo(3));
    }
}
