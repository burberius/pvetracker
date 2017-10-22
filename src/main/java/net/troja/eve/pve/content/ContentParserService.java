package net.troja.eve.pve.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import net.troja.eve.pve.price.PriceService;

@Service
public class ContentParserService {
    private static final Logger LOGGER = LogManager.getLogger(ContentParserService.class);
    private static final int EXPECTED_COLUMN_COUNT = 2;

    @Autowired
    private TypeTranslationRepository translationsRepository;
    @Autowired
    private PriceService priceService;

    public List<LootBean> parse(final String content) {
        if (!StringUtils.isBlank(content)) {
            return processContent(content);

        }
        return Collections.emptyList();
    }

    private List<LootBean> processContent(final String content) {
        final List<LootBean> lootList = new ArrayList<>();
        final List<Integer> lootTypeIds = new ArrayList<>();
        for (final String line : content.split("\n")) {
            try {
                final String[] cols = line.split("\t");
                if (cols.length < EXPECTED_COLUMN_COUNT) {
                    LOGGER.warn("Wrong number of columns: " + line);
                }
                String name = cols[0].trim();
                if (name.endsWith("*")) {
                    name = name.substring(0, name.length() - 1);
                }
                final List<TypeTranslationBean> result = translationsRepository.findByName(name);
                if (result.isEmpty()) {
                    continue;
                }
                final int quantity = Integer.parseInt(cols[1].replaceAll("[.,]", ""));
                final int typeId = result.get(0).getTypeId();
                lootList.add(new LootBean(typeId, name, quantity));
                lootTypeIds.add(typeId);
            } catch (final NumberFormatException e) {
                LOGGER.warn("Could not parse pasted content: " + e.getMessage() + " of line '" + line + "'");
            }
        }
        addPrices(lootList, lootTypeIds);
        return lootList;
    }

    private void addPrices(final List<LootBean> lootList, final List<Integer> lootTypeIds) {
        final Map<Integer, Double> prices = priceService.getPrices(lootTypeIds);
        for (final LootBean loot : lootList) {
            final Double value = prices.get(loot.getTypeId());
            if (value != null) {
                loot.setValue(value);
            }
        }
    }

    void setTranslationsRepository(final TypeTranslationRepository translationsRepository) {
        this.translationsRepository = translationsRepository;
    }

    void setPriceService(final PriceService priceService) {
        this.priceService = priceService;
    }
}
