package net.troja.eve.pve.price;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.price.contract.ContractPriceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Needs a fix")
class ContractPriceServiceTest {
    private final ContractPriceService classToTest = new ContractPriceService();

    @Test
    public void getPrices() {
        classToTest.setRestTemplate(new RestTemplate());

        int typeId = 17716;
        final PriceBean price = classToTest.getPrice(typeId);

        assertThat(price).isNotNull();
        assertThat(price.getTypeId()).isEqualTo(typeId);
        assertThat(price.getValue()).isGreaterThan(50_000_000);
    }
}