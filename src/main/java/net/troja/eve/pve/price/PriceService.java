package net.troja.eve.pve.price;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.troja.eve.pve.db.price.PriceBean;
import net.troja.eve.pve.db.price.PriceRepository;

@Service
public class PriceService {
    private static final int INITIAL_DELAY_10S = 10_000;
    private static final int DELETE_INTERVAL_1HOUR = 3_600_000;

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
        final Map<Integer, Double> result = new ConcurrentHashMap<>(prices.size());
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

    @Scheduled(fixedRate = DELETE_INTERVAL_1HOUR, initialDelay = INITIAL_DELAY_10S)
    public void deleteOld() {
        priceRepository.deleteByCreatedBefore(LocalDateTime.now().minusHours(2));
    }

    void setFuzzworkPriceService(final FuzzworkPriceService fuzzworkPriceService) {
        this.fuzzworkPriceService = fuzzworkPriceService;
    }

    void setPriceRepository(final PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }
}
