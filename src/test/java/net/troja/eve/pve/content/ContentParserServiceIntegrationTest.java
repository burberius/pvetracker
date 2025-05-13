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

import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import net.troja.eve.pve.price.PriceService;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class ContentParserServiceIntegrationTest {
    @Autowired
    private TypeTranslationRepository translationsRepository;
    @Mock
    private PriceService priceService;

    private ContentParserService classToTest;

    private List<LootBean> reference;
    private Map<Integer, Double> prices;

    @BeforeEach
    void setUp() {
        classToTest = new ContentParserService(translationsRepository, priceService);
        if (reference == null) {
            generateReference();
        }
        if (prices == null) {
            generatePrices();
        }
        when(priceService.getPrices(anyList())).thenReturn(prices);
    }

    @ParameterizedTest
    @ValueSource(strings = { "en", "de", "fr", "ja", "ru" })
    void parseLang(String lang) {
        final List<LootBean> list = parseLangFile(lang);

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(8));
        if(!lang.equals("fr") && !lang.equals("ja")) {
            assertThat(list.toString(), equalTo(reference.toString()));
        }
    }

    private List<LootBean> parseLangFile(final String lang) {
        final Path path = Paths.get("src/test/resources/content_" + lang + ".txt");
        final String content = Files.contentOf(path.toFile(), "UTF-8");
        return classToTest.parse(content);
    }

    private void generateReference() {
        reference = new ArrayList<>();
        reference.add(new LootBean(27401, "Caldari Navy Nova Heavy Assault Missile", 16000, 123.45d));
        reference.add(new LootBean(28363, "Drone Cerebral Fragment", 47, 551.2d));
        reference.add(new LootBean(28366, "Drone Coronary Unit", 20, 0.123d));
        reference.add(new LootBean(28364, "Drone Tactical Limb", 23, 51341.44d));
        reference.add(new LootBean(21815, "Elite Drone AI", 1, 1d));
        reference.add(new LootBean(28668, "Nanite Repair Paste", 174, 623.1111d));
        reference.add(new LootBean(24533, "Scourge Fury Cruise Missile", 20743, 67245.0d));
        reference.add(new LootBean(15466, "Standard Drop Booster", 1, 5234.3d));
    }

    private void generatePrices() {
        prices = new HashMap<>();
        for (final LootBean loot : reference) {
            prices.put(loot.getTypeId(), loot.getValue());
        }
    }
}
