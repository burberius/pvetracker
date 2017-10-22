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

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import static org.junit.Assert.assertThat;

import net.troja.eve.pve.db.price.PriceBean;

public class FuzzworkPriceServiceTest {
    private final FuzzworkPriceService classToTest = new FuzzworkPriceService();

    @Test
    public void getPrices() {
        classToTest.setRestTemplate(new RestTemplate());

        final List<PriceBean> prices = classToTest.getPrices(Arrays.asList(34, 35));

        assertThat(prices.size(), equalTo(2));
        assertThat(prices.get(0).getValue(), greaterThan(4D));
        assertThat(prices.get(1).getValue(), greaterThan(4D));
    }
}
