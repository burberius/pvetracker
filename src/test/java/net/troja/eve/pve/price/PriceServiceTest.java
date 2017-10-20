package net.troja.eve.pve.price;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.db.price.PriceRepository;

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
}
