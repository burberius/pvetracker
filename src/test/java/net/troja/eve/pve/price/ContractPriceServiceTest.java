package net.troja.eve.pve.price;

import net.troja.eve.pve.discord.DiscordService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled("Takes too long")
@ExtendWith(MockitoExtension.class)
class ContractPriceServiceTest {
    @Mock
    private DiscordService discordService;
    @InjectMocks
    private ContractPriceService classToTest;

    @Test
    void updateContractPrice() {
        classToTest.setTestRun(true);
        classToTest.updateContracts();
        verify(discordService, times(3)).sendMessage(anyString());

        Map<Integer, List<Double>> typePrices = classToTest.getTypePrices();
        assertThat(typePrices).hasSizeGreaterThan(100);

        Map.Entry<Integer, List<Double>> entry = typePrices.entrySet().stream().findFirst().get();
        assertThat(classToTest.getPrice(entry.getKey()).getValue())
                .isEqualTo(entry.getValue().stream().min(Double::compareTo).get());
    }
}
