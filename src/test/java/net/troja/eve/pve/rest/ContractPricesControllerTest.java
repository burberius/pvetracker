package net.troja.eve.pve.rest;

import net.troja.eve.pve.SecurityConfiguration;
import net.troja.eve.pve.db.account.AccountRepository;
import net.troja.eve.pve.price.ContractPriceService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContractPricesController.class)
@Import({SecurityConfiguration.class})
class ContractPricesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRepository accountRepository;
    @MockitoBean
    private ContractPriceService contractPriceService;

    @Test
    void getLastUpdate() throws Exception {
        when(contractPriceService.getLastUpdate()).thenReturn(123L);
        mockMvc.perform(get("/contractprices/lastupdate")).andExpect(status().isOk())
                .andExpect(content().string(containsString("123")));
    }

    @Test
    void getAllContractPrices() throws Exception {
        Map<Integer, Long> content = Map.of(123, 456L);
        when(contractPriceService.getAllContractPrices()).thenReturn(content);
        mockMvc.perform(get("/contractprices")).andExpect(status().isOk())
                .andExpect(content().json("{\"123\":456}"));
    }
}
