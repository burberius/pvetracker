package net.troja.eve.pve.web;

import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;
import net.troja.eve.pve.price.PriceService;
import net.troja.eve.pve.sso.EveOAuth2User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private Model model;
    @Mock
    private OAuth2AuthenticationToken principal;
    @Mock
    private OutcomeRepository outcomeRepo;
    @Mock
    private PriceService priceService;
    @InjectMocks
    private StatsController classToTest;

    @Test
    void getStats() {
        EveOAuth2User eveOAuth2User = mock(EveOAuth2User.class);
        final AccountBean account = new AccountBean();
        when(eveOAuth2User.getAccount()).thenReturn(account);
        when(principal.getPrincipal()).thenReturn(eveOAuth2User);
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
