package net.troja.eve.pve.price;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.model.PublicContractsItemsResponse;
import net.troja.eve.esi.model.PublicContractsResponse;
import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.discord.DiscordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.troja.eve.pve.esi.GeneralEsiService.DATASOURCE;

@Service
@Log4j2
@RequiredArgsConstructor
public class ContractPriceService {
    private static final int THE_FORGE = 10000002;

    private final DiscordService discordService;

    private final ContractsApi contractsApi = new ContractsApi();
    @Getter(AccessLevel.PACKAGE)
    private final Map<Integer, List<Double>> typePrices = new HashMap<>();
    private int statsContractsProcessed;
    @Setter(AccessLevel.PACKAGE)
    private boolean isTestRun = false;

    @Scheduled(initialDelay = 10000, fixedRate = 21600000)
    public void updateContracts() {
        typePrices.clear();
        int numberOfContracts = 0;
        int page = 1;
        statsContractsProcessed = 0;
        discordService.sendMessage("Starting public contract price update");
        try {
            List<PublicContractsResponse> contracts;
            do {
                contracts = contractsApi.getContractsPublicRegionId(THE_FORGE, DATASOURCE, null, page);
                contracts.stream()
                        .filter(contract -> contract.getType() == PublicContractsResponse.TypeEnum.ITEM_EXCHANGE)
                        .forEach(this::processContract);
                page++;
                numberOfContracts += contracts.size();
                if(isTestRun && page > 2) {
                    break;
                }
            } while(!contracts.isEmpty());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        discordService.sendMessage("Read " + numberOfContracts + " public contracts on " + (page - 1) + " pages and processed " + statsContractsProcessed);
        discordService.sendMessage("There are now " + typePrices.size() + " prices available from contracts");
    }

    private void processContract(PublicContractsResponse contract) {
        Double price = contract.getPrice();
        if (price != null && price > 0.0) {
            try {
                List<PublicContractsItemsResponse>items = contractsApi.getContractsPublicItemsContractId(contract.getContractId(), DATASOURCE, null, 1);
                if (items != null && items.size() == 1) {
                    PublicContractsItemsResponse item = items.getFirst();
                    typePrices.computeIfAbsent(item.getTypeId(), k -> new ArrayList<>()).add(price);
                    statsContractsProcessed++;
                }
            } catch (ApiException e) {
                log.info("Could not get contract content: {}", e.getMessage());
            }
        }
    }

    public PriceBean getPrice(int typeId) {
        List<Double> prices = typePrices.get(typeId);
        if(prices != null && !prices.isEmpty()) {
            return new PriceBean(typeId, prices.stream().min(Double::compareTo).get());
        } else {
            return null;
        }
    }
}
