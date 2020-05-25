package net.troja.eve.pve.content;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import net.troja.eve.pve.price.PriceService;

public class ContentParserServiceTest {
    private ContentParserService classToTest;

    @Mock
    private TypeTranslationRepository repository;
    @Mock
    private PriceService priceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest = new ContentParserService();
        classToTest.setTranslationsRepository(repository);
        classToTest.setPriceService(priceService);
    }

    @Test
    public void parseNull() {
        final List<LootBean> list = classToTest.parse(null);

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(0));
    }

    @Test
    public void parseEmpty() {
        final List<LootBean> list = classToTest.parse(" ");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(0));
    }

    @Test
    public void parseRubbish() {
        final List<LootBean> list = classToTest.parse("Nothing   in      here");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(0));
    }

    @Test
    public void parseNotNumberQuantity() {
        final List<TypeTranslationBean> transList = new ArrayList<>();
        transList.add(new TypeTranslationBean());
        when(repository.findByName("Nanite Repair Paste")).thenReturn(transList);

        final String wrong = "Nanite Repair Paste\t174a\tNanite Repair Paste\t\t\t1,74 m3";
        final List<LootBean> list = classToTest.parse(wrong);

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(0));
    }

    @Test
    public void parseDifferentNumberFormats() {
        final List<TypeTranslationBean> transList = new ArrayList<>();
        final TypeTranslationBean translationBean = new TypeTranslationBean();
        translationBean.setTypeId(1);
        transList.add(translationBean);
        when(repository.findByName("Nanite Repair Paste")).thenReturn(transList);
        final Map<Integer, Double> prices = new HashMap<>();
        prices.put(1, 1.2d);
        when(priceService.getPrices(Arrays.asList(1))).thenReturn(prices);

        final String content = "Nanite Repair Paste\t174,00\tNanite Repair Paste\t\t\t1,74 m3";
        final List<LootBean> list = classToTest.parse(content);

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(1));
        final LootBean result = list.get(0);
        assertThat(result.getCount(), equalTo(17400));
        assertThat(result.getValue(), equalTo(1.2d));
    }

    @Test
    public void parseWithBlueprint() {
        final String content = "Cynabal Blueprint\t\tCruiser Blueprint\t\t\t0,01 m3 \n"
                + "Scourge Fury Heavy Missile\t864\tAdvanced Heavy Missile\t\t\t25,92 m3\t394.251,84 ISK" +
                "Gistum B-Type Adaptive Invulnerability Shield Hardener\t1\tShield Hardener\tMedium\t5 m3\t340.247.434,12 ISK";
        final List<TypeTranslationBean> transList1 = new ArrayList<>();
        final TypeTranslationBean trans1 = new TypeTranslationBean();
        trans1.setTypeId(1);
        transList1.add(trans1);
        when(repository.findByName("Cynabal Blueprint")).thenReturn(transList1);

        final List<TypeTranslationBean> transList2 = new ArrayList<>();
        final TypeTranslationBean trans2 = new TypeTranslationBean();
        trans2.setTypeId(2);
        transList2.add(trans2);
        when(repository.findByName("Scourge Fury Heavy Missile")).thenReturn(transList2);
        final Map<Integer, Double> prices = new HashMap<>();
        prices.put(2, 1.2d);
        when(priceService.getPrices(Arrays.asList(1, 2))).thenReturn(prices);

        final List<LootBean> list = classToTest.parse(content);

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(2));
    }
}
