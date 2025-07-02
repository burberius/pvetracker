package net.troja.eve.pve.db.outcome;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;
import net.troja.eve.pve.db.stats.SiteCountStatBean;
import net.troja.eve.pve.db.stats.ValuesStatBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutcomeRepository extends CrudRepository<OutcomeBean, Long> {
    List<OutcomeBean> findByAccountOrderByStartTimeDesc(AccountBean account);

    @Query(value = "select o from OutcomeBean o where (o.bountyValue + o.rewardValue + o.lootValue) > 0 and o.account = :account order by o.startTime desc")
    List<OutcomeBean> findLastSiteEarnings(@Param(value = "account") AccountBean account, Pageable pageable);

    @Query(
        value = "select new net.troja.eve.pve.db.stats.SiteCountStatBean(o.siteName, count(o.id)) from OutcomeBean o "
                + "where o.site.id > 0 and o.account = :account group by o.siteName order by count(o.id)")
    List<SiteCountStatBean> getSiteCountStats(@Param(value = "account") AccountBean account, Pageable pageable);

    @Query(value = "select new net.troja.eve.pve.db.stats.MonthOverviewStatBean(cast(o.startTime as LocalDate), sum(o.lootValue), sum(o.bountyValue), sum(o.rewardValue)) from OutcomeBean o where "
            + "o.account = :account and o.startTime > :start group by cast(o.startTime as LocalDate) order by cast(o.startTime as LocalDate)")
    List<MonthOverviewStatBean> getMonthlyOverviewStats(@Param(value = "account") AccountBean account, @Param(value = "start") LocalDateTime start);

    @Query(
        value = "select new net.troja.eve.pve.db.stats.ValuesStatBean(sum(rewardValue), sum(bountyValue), "
                + "sum(lootValue)) from OutcomeBean where account = :account and startTime > :start")
    ValuesStatBean getValueStats(@Param(value = "account") AccountBean account, @Param(value = "start") LocalDateTime start);
}
