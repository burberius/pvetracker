package net.troja.eve.pve.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.ui.Model;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;

public class StatsControllerTest {
    private final StatsController classToTest = new StatsController();

    @Mock
    private Model model;
    @Mock
    private OAuth2Authentication principal;
    @Mock
    private OutcomeRepository outcomeRepo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setOutcomeRepo(outcomeRepo);
    }

    @Test
    @Ignore
    public void getStats() {
        final AccountBean account = new AccountBean();
        when(principal.getPrincipal()).thenReturn(account);
        final LocalDate now = LocalDate.now();
        final MonthOverviewStatBean statBean = new MonthOverviewStatBean(now, 123456789D);
        when(outcomeRepo.getMonthlyOverviewStats(eq(account), any(LocalDateTime.class))).thenReturn(Arrays.asList(statBean));

        final String stats = classToTest.getStats(model, principal);

        final ArgumentCaptor<ChartDataBean> argument = ArgumentCaptor.forClass(ChartDataBean.class);
        verify(model).addAttribute(eq("montly"), argument.capture());

        final ChartDataBean result = argument.getValue();
        assertThat(result.getData().size(), equalTo(31));
        assertThat(result.getData().get(30), equalTo(123.46D));
        assertThat(result.getLabels().get(30), equalTo(now.toString()));
        assertThat(stats, equalTo("stats"));
    }
}
