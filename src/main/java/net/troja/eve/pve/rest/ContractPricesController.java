package net.troja.eve.pve.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.troja.eve.pve.price.ContractPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ContractPricesController {
    private final ContractPriceService contractPriceService;

    @GetMapping("/contractprices")
    public Map<Integer, Long> getContractPrices() {
        return contractPriceService.getAllContractPrices();
    }

    @GetMapping("/contractprices/lastupdate")
    public Long getLastUpdate() {
        return contractPriceService.getLastUpdate();
    }
}
