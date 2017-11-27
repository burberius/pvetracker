package net.troja.eve.pve.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;

@Controller
@RequestMapping("/stats")
public class StatsController {
    private static final int NUMBER_OF_DAYS_IN_MONTH = 30;

    private static final double VALUE_100 = 100D;

    private static final double VALUE_10K = 10_000D;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private OutcomeRepository outcomeRepository;

    public StatsController() {
        super();
    }

    @GetMapping
    public String getStats(final Model model, final Principal principal) {
        final AccountBean account = (AccountBean) ((OAuth2Authentication) principal).getPrincipal();
        final LocalDateTime start = LocalDateTime.now().minusDays(NUMBER_OF_DAYS_IN_MONTH);
        final List<MonthOverviewStatBean> monthlyOverviewStats = outcomeRepository.getMonthlyOverviewStats(account, start);
        model.addAttribute("montly", convertMonthlyData(monthlyOverviewStats));
        return "stats";
    }

    private static ChartDataBean convertMonthlyData(final List<MonthOverviewStatBean> data) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        final ChartDataBean result = new ChartDataBean();
        LocalDate start = LocalDate.now().minusDays(NUMBER_OF_DAYS_IN_MONTH);
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
