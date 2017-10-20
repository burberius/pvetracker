package net.troja.eve.pve.price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.db.price.PriceRepository;

@Service
public class PriceService {
    private static final Logger LOGGER = LogManager.getLogger(PriceService.class);

    @Autowired
    private FuzzworkPriceService fuzzworkPriceService;
    @Autowired
    private PriceRepository priceRepository;

    public PriceService() {
        super();
    }

    public Map<Integer, Double> getPrices(final List<Integer> prices) {
        LOGGER.info("getPrices: {}", prices);
        final Map<Integer, Double> result = new HashMap<>();
        final Iterable<PriceBean> allById = priceRepository.findAllById(prices);
        final Set<Integer> rest = new HashSet<>(prices);
        for (final PriceBean price : allById) {
            result.put(price.getTypeId(), price.getValue());
            rest.remove(price.getTypeId());
        }
        LOGGER.info("retrieve Prices: {}", rest);
        final List<PriceBean> list = fuzzworkPriceService.getPrices(new ArrayList<>(rest));
        priceRepository.saveAll(list);
        for (final PriceBean price : list) {
            result.put(price.getTypeId(), price.getValue());
        }
        return result;
    }

    void setFuzzworkPriceService(final FuzzworkPriceService fuzzworkPriceService) {
        this.fuzzworkPriceService = fuzzworkPriceService;
    }

    void setPriceRepository(final PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }
}
