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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.stats.MonthOverviewStat;
import net.troja.eve.pve.db.stats.SiteCountStat;

public interface OutcomeRepository extends CrudRepository<OutcomeBean, Long> {
    List<OutcomeBean> findByAccountOrderByStartDesc(AccountBean account);

    @Query(
        value = "select new net.troja.eve.pve.db.stats.SiteCountStat(o.site.name, count(o)) from OutcomeBean o "
                + "where site != null and account = :account group by site order by count(id) desc")
    List<SiteCountStat> getSiteCountStats(@Param(value = "account") AccountBean account, Pageable pageable);

    @Query(
        value = "select new net.troja.eve.pve.db.stats.MonthOverviewStat(DATE(start),  sum(loot_value + "
                + "bounty_value + reward_value)) from OutcomeBean o where account = :account and start > :start"
                + " group by DATE(start) order by DATE(start)",
        nativeQuery = true)
    List<MonthOverviewStat> getMonthlyOverviewStats(@Param(value = "account") AccountBean account, @Param(value = "start") LocalDateTime start);
}
