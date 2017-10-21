package net.troja.eve.pve.price;

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
