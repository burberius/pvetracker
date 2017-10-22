package net.troja.eve.pve.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.Assert.assertThat;

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

    @Before
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
}
