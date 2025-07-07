package net.troja.eve.pve.price;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.model.PublicContractsItemsResponse;
import net.troja.eve.esi.model.PublicContractsResponse;
import net.troja.eve.pve.db.contract.ContractBean;
import net.troja.eve.pve.db.contract.ContractRepository;
import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.discord.DiscordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.ZoneOffset.UTC;
import static net.troja.eve.pve.ApiUtils.getPagesMax;
import static net.troja.eve.pve.esi.GeneralEsiService.DATASOURCE;

@Service
@Log4j2
@RequiredArgsConstructor
public class ContractPriceService {
    private static final int THE_FORGE = 10000002;

    private final DiscordService discordService;
    private final ContractRepository contractRepository;

    private final ContractsApi contractsApi = new ContractsApi();
    @Getter(AccessLevel.PACKAGE)
    private int statsContractsProcessed;
    private int statsContractItemsCalls;
    @Setter(AccessLevel.PACKAGE)
    private boolean isTestRun = false;
    private Set<Integer> allContractIds;

    @Scheduled(cron = "0 20 5,11,17,23 * * ?")
    public void updateContracts() {
        contractRepository.deleteByDateExpiredBefore(OffsetDateTime.now(UTC));
        int numberOfContracts = 0;
        int page = 1;
        int pagesMax = 0;
        statsContractsProcessed = 0;
        statsContractItemsCalls = 0;
        allContractIds = new HashSet<>(contractRepository.getAllContractIds());
        discordService.sendMessage("Starting public contract price update");
        try {
            ApiResponse<List<PublicContractsResponse>> firstRequest =
                    contractsApi.getContractsPublicRegionIdWithHttpInfo(THE_FORGE, DATASOURCE, null, page);
            pagesMax = getPagesMax(firstRequest);
            log.info("Max pages {}, {} contracts before", pagesMax, allContractIds.size());
            while (page <= pagesMax) {
                List<PublicContractsResponse> contracts =
                        contractsApi.getContractsPublicRegionId(THE_FORGE, DATASOURCE, null, page);
                log.info("Processing page {} with {} contracts", page, contracts.size());
                contracts.stream()
                        .filter(contract -> contract.getType() == PublicContractsResponse.TypeEnum.ITEM_EXCHANGE)
                        .forEach(this::processContract);
                numberOfContracts += contracts.size();
                page++;
                if(isTestRun && page > 2) {
                    break;
                }
            }
        } catch (ApiException e) {
            log.warn("Could not get contracts: {}", e.getMessage(), e);
        }
        log.info("{} contracts left from the old ones", allContractIds.size());
        allContractIds.forEach(contractRepository::deleteById);
        String result = "Read " + numberOfContracts + " public contracts on " + pagesMax +
                " pages and processed " + statsContractsProcessed + " ones\n" +
                "There are now prices for " + contractRepository.countTypeIds() + " types available from contracts\n" +
                statsContractItemsCalls + " contract items calls needed, " + allContractIds.size() +
                " old contracts removed";
        discordService.sendMessage(result);
    }

    private void processContract(PublicContractsResponse contract) {
        Double price = contract.getPrice();
        allContractIds.remove(contract.getContractId());
        if (price != null && price > 0.0 && contractRepository.findById(contract.getContractId()).isEmpty()) {
            try {
                List<PublicContractsItemsResponse>items = contractsApi.getContractsPublicItemsContractId(contract.getContractId(), DATASOURCE, null, 1);
                statsContractItemsCalls++;
                if (items != null && items.size() == 1) {
                    PublicContractsItemsResponse item = items.getFirst();
                    if (item.getIsIncluded()) {
                        ContractBean contractBean = new ContractBean(contract.getContractId(), item.getTypeId(), price,
                                contract.getDateExpired());
                        contractRepository.save(contractBean);
                        statsContractsProcessed++;
                    } else {
                        contractRepository.deleteById(contract.getContractId());
                    }
                }
            } catch (ApiException e) {
                log.info("Could not get contract content for ID {}", contract.getContractId());
            }
        }
    }

    public PriceBean getPrice(int typeId) {
        Optional<Double> lowestPriceByTypeId = contractRepository.findLowestPriceByTypeId(typeId);
        return lowestPriceByTypeId.map(aDouble -> new PriceBean(typeId, aDouble)).orElse(null);
    }
}
