package net.troja.eve.pve.web;

import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;
import net.troja.eve.pve.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stats")
public class StatsController {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(DATE_FORMAT, PvEApplication.DEFAULT_LOCALE);
    private static final int TYPE_ID_PLEX = 44992;
    private static final int NUMBER_OF_DAYS_IN_MONTH = 30;
    private static final double VALUE_100 = 100D;
    private static final double VALUE_10K = 10_000D;


    @Autowired
    private OutcomeRepository outcomeRepository;
    @Autowired
    private PriceService priceService;

    public StatsController() {
        super();
    }

    @GetMapping
    public String getStats(final Model model, final Principal principal) {
        AccountBean account = ControllerHelper.getAccount(principal);
        final LocalDateTime start = LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusDays(NUMBER_OF_DAYS_IN_MONTH);
        final List<MonthOverviewStatBean> monthlyOverviewStats = outcomeRepository.getMonthlyOverviewStats(account, start);
        model.addAttribute("montly", convertMonthlyData(monthlyOverviewStats));
        model.addAttribute("lastValueSites", outcomeRepository.findLastSiteEarnings(account, PageRequest.of(0, 10)));
        model.addAttribute("values", outcomeRepository.getValueStats(account, start));
        final Map<Integer, Double> prices = priceService.getPrices(Arrays.asList(TYPE_ID_PLEX));
        model.addAttribute("plex", prices.get(TYPE_ID_PLEX));
        return "stats";
    }

    private static ChartDataBean convertMonthlyData(final List<MonthOverviewStatBean> data) {
        final ChartDataBean result = new ChartDataBean();
        if(data.isEmpty()) {
            return result;
        }
        LocalDate start = LocalDate.now(PvEApplication.DEFAULT_ZONE).minusDays(NUMBER_OF_DAYS_IN_MONTH);
        final Iterator<MonthOverviewStatBean> iterator = data.iterator();
        MonthOverviewStatBean stat = iterator.next();
        for (int days = 0; days <= NUMBER_OF_DAYS_IN_MONTH; days++) {
            final String dateString = formatter.format(start);
            double value = 0D;
            if (stat != null && formatter.format(stat.getDate()).equals(dateString)) {
                value = Math.round(stat.getValue() / VALUE_10K) / VALUE_100;
                if (iterator.hasNext()) {
                    stat = iterator.next();
                } else {
                    stat = null;
                }
            }
            result.addLabel(dateString);
            result.addData(value);
            start = start.plusDays(1);
        }
        return result;
    }

    public void setOutcomeRepo(final OutcomeRepository outcomeRepo) {
        outcomeRepository = outcomeRepo;
    }
}
