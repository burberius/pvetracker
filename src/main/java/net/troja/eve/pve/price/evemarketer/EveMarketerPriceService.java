package net.troja.eve.pve.price.evemarketer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.price.AbstractOnlinePriceService;

@Service
public class EveMarketerPriceService extends AbstractOnlinePriceService<List<EveMarketerPriceBean>> {
    private static final String ADDRESS = "https://api.evemarketer.com/ec/marketstat/json?usesystem=30000142&typeid=";

    public EveMarketerPriceService() {
        super();
    }

    @Override
    protected ResponseEntity<List<EveMarketerPriceBean>> queryPrices(final String values) {
        final ParameterizedTypeReference<List<EveMarketerPriceBean>> responseType = new ParameterizedTypeReference<List<EveMarketerPriceBean>>() {
        };
        return restTemplate.exchange(ADDRESS + values, HttpMethod.GET, getHttpEntity(), responseType);
    }

    @Override
    protected List<PriceBean> transform(final List<EveMarketerPriceBean> prices) {
        final List<PriceBean> result = new ArrayList<>(prices.size());
        for (final EveMarketerPriceBean price : prices) {
            final int[] types = price.getSell().getForQuery().getTypes();
            final double value = price.getSell().getFivePercent();
            if (value > 0) {
                result.add(new PriceBean(types[0], value));
            }
        }
        return result;
    }
}
