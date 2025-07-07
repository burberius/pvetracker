package net.troja.eve.pve.price;

import net.troja.eve.pve.db.contract.ContractRepository;
import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.discord.DiscordService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractPriceServiceTest {
    public static final int TYPE_ID = 123;
    @Mock
    private DiscordService discordService;
    @Mock
    private ContractRepository contractRepository;
    @InjectMocks
    private ContractPriceService classToTest;

    @Test
    @Disabled("Takes too long")
    void updateContractPrice() {
        classToTest.setTestRun(true);
        classToTest.updateContracts();
        verify(discordService, times(3)).sendMessage(anyString());
        verify(contractRepository, atLeastOnce()).save(any());
    }

    @Test
    void getContractPriceFound() {
        double value = 123.0;
        when(contractRepository.findLowestPriceByTypeId(TYPE_ID)).thenReturn(Optional.of(value));
        PriceBean price = classToTest.getPrice(TYPE_ID);

        assertThat(price).isNotNull();
        assertThat(price.getValue()).isEqualTo(value);
    }

    @Test
    void getContractPriceNotFound() {
        when(contractRepository.findLowestPriceByTypeId(TYPE_ID)).thenReturn(Optional.empty());
        PriceBean price = classToTest.getPrice(TYPE_ID);

        assertThat(price).isNull();
    }
}
