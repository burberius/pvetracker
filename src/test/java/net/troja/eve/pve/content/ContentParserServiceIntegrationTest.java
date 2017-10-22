package net.troja.eve.pve.content;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import net.troja.eve.pve.price.PriceService;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ContentParserServiceIntegrationTest {
    private final ContentParserService classToTest = new ContentParserService();

    @Autowired
    private TypeTranslationRepository translationsRepository;
    @Mock
    private PriceService priceService;
    private List<LootBean> reference;
    private Map<Integer, Double> prices;
    private final List<Integer> typeIds = Arrays.asList(27401, 28363, 28366, 28364, 21815, 28668, 24533, 15466);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setTranslationsRepository(translationsRepository);
        classToTest.setPriceService(priceService);
        if (reference == null) {
            generateReference();
        }
        if (prices == null) {
            generatePrices();
        }
        when(priceService.getPrices(typeIds)).thenReturn(prices);
    }

    @Test
    public void parseEn() {
        final List<LootBean> list = parseLangFile("en");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(8));
        assertThat(list.toString(), equalTo(reference.toString()));
    }

    @Test
    public void parseDe() {
        final List<LootBean> list = parseLangFile("de");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(8));
        assertThat(list.toString(), equalTo(reference.toString()));
    }

    @Test
    public void parseFr() {
        final List<LootBean> list = parseLangFile("fr");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(8));
    }

    @Test
    public void parseJa() {
        final List<LootBean> list = parseLangFile("ja");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(8));
    }

    @Test
    public void parseRu() {
        final List<LootBean> list = parseLangFile("ru");

        assertThat(list, notNullValue());
        assertThat(list.size(), equalTo(8));
    }

    private List<LootBean> parseLangFile(final String lang) {
        final Path path = Paths.get("src/test/resources/content_" + lang + ".txt");
        final String content = Files.contentOf(path.toFile(), "UTF-8");
        final List<LootBean> list = classToTest.parse(content);
        return list;
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
