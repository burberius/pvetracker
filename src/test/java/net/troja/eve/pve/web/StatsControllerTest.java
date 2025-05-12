package net.troja.eve.pve.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import net.troja.eve.pve.PvEApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.ui.Model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;

@ExtendWith(MockitoExtension.class)
public class StatsControllerTest {
    @Mock
    private Model model;
    @Mock
    private OAuth2AuthorizationCodeAuthenticationToken principal;
    @Mock
    private OutcomeRepository outcomeRepo;
    @InjectMocks
    private StatsController classToTest;

    @Test
    @Disabled
    public void getStats() {
        final AccountBean account = new AccountBean();
        when(principal.getPrincipal()).thenReturn(account);
        final LocalDate now = LocalDate.now(PvEApplication.DEFAULT_ZONE);
        final MonthOverviewStatBean statBean = new MonthOverviewStatBean(now, 123456789L);
        when(outcomeRepo.getMonthlyOverviewStats(eq(account), any(LocalDateTime.class))).thenReturn(List.of(statBean));

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
