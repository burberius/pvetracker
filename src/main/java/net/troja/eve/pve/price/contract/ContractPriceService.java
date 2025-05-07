package net.troja.eve.pve.price.contract;

import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.price.AbstractOnlinePriceService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractPriceService extends AbstractOnlinePriceService<ContractPriceBean> {
    private static final String ADDRESS_START = "https://api.contractsappraisal.com/v1/prices/";
    private static final String ADDRESS_END = "?include_private=false&bpc=true&security=highsec&material_efficiency=0&time_efficiency=0";

    public ContractPriceService() {
        super();
    }

    @Override
    protected ResponseEntity<ContractPriceBean> queryPrices(String value) {
        /*final ParameterizedTypeReference<ContractPriceBean> responseType =
                new ParameterizedTypeReference<ContractPriceBean>() {
                };
        return restTemplate.exchange(ADDRESS_START + value + ADDRESS_END, HttpMethod.GET, getHttpEntity(), responseType);*/
        return null;
    }

    @Override
    protected List<PriceBean> transform(ContractPriceBean price) {
        return List.of(new PriceBean(price.getTypeId(), price.getFivePercent()));
    }

    public PriceBean getPrice(int typeId) {
        List<PriceBean> prices = getPrices(List.of(typeId));
        if(prices != null && !prices.isEmpty()) {
            return prices.get(0);
        } else {
            return null;
        }
    }
}
