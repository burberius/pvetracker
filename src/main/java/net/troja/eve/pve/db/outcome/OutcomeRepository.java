package net.troja.eve.pve.db.outcome;

import java.time.LocalDateTime;

/*
 * ====================================================
 * Eve Online PvE Tracker
 * ----------------------------------------------------
 * Copyright (C) 2017 Jens Oberender <j.obi@troja.net>
 * ----------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ====================================================
 */

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;
import net.troja.eve.pve.db.stats.SiteCountStatBean;
import net.troja.eve.pve.db.stats.ValuesStatBean;

public interface OutcomeRepository extends CrudRepository<OutcomeBean, Long> {
    List<OutcomeBean> findByAccountOrderByStartTimeDesc(AccountBean account);

    @Query(value = "select o from OutcomeBean o where (o.bountyValue + o.rewardValue + o.lootValue) > 0 and o.account = :account order by o.startTime desc")
    List<OutcomeBean> findLastSiteEarnings(@Param(value = "account") AccountBean account, Pageable pageable);

    @Query(
        value = "select new net.troja.eve.pve.db.stats.SiteCountStatBean(o.siteName, count(o.id)) from OutcomeBean o "
                + "where o.site.id > 0 and o.account = :account group by o.siteName")
    List<SiteCountStatBean> getSiteCountStats(@Param(value = "account") AccountBean account, Pageable pageable);

    @Query(value = "select new net.troja.eve.pve.db.stats.MonthOverviewStatBean(cast(o.startTime as LocalDate), sum(o.lootValue), sum(o.bountyValue), sum(o.rewardValue)) from OutcomeBean o where "
            + "o.account = :account and o.startTime > :start group by cast(o.startTime as LocalDate) order by cast(o.startTime as LocalDate)")
    List<MonthOverviewStatBean> getMonthlyOverviewStats(@Param(value = "account") AccountBean account, @Param(value = "start") LocalDateTime start);

    @Query(
        value = "select new net.troja.eve.pve.db.stats.ValuesStatBean(sum(rewardValue), sum(bountyValue), "
                + "sum(lootValue)) from OutcomeBean where account = :account and startTime > :start")
    ValuesStatBean getValueStats(@Param(value = "account") AccountBean account, @Param(value = "start") LocalDateTime start);
}
