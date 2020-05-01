package net.troja.eve.pve.db.outcome;

import java.time.Duration;
import java.time.LocalDate;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import lombok.Data;
import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;

@SqlResultSetMapping(
    name = "MonthlyOverviewStatsMapping",
    classes = {
        @ConstructorResult(
            targetClass = MonthOverviewStatBean.class,
            columns = { @ColumnResult(name = "date", type = LocalDate.class), @ColumnResult(name = "value", type = Double.class) }) })

@NamedNativeQuery(
    name = "OutcomeBean.getMonthlyOverviewStats",
    query = "select DATE(start) as date, sum(loot_value + bounty_value + reward_value) as value from outcome o where "
            + "account_id = :account and start > :start group by DATE(start) order by DATE(start)",
    resultSetMapping = "MonthlyOverviewStatsMapping")
@Data
@Entity
@Table(name = "outcome")
public class OutcomeBean {
    private static final int SECONDS2MINUTES = 60;
    private static final int SECONDS2HOURS = 3600;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountBean account;
    @ManyToOne
    @JoinColumn(name = "system_id")
    private SolarSystemBean system;
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private ShipBean ship;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteBean site;
    private String siteName;
    private LocalDateTime start = LocalDateTime.now(PvEApplication.DEFAULT_ZONE);
    private LocalDateTime end;
    private boolean faction;
    private boolean escalation;
    private long bountyValue;
    private long rewardValue;
    private long lootValue;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "outcome_id")
    private List<LootBean> loot;

    public OutcomeBean() {
        super();
    }

    public OutcomeBean(final AccountBean account, final SolarSystemBean system, final ShipBean ship, final String siteName, final SiteBean site) {
        super();
        this.account = account;
        this.system = system;
        this.ship = ship;
        this.siteName = siteName;
        this.site = site;
    }

    public void addLoot(final LootBean lootEntry) {
        if (loot == null) {
            loot = new ArrayList<>();
        }
        loot.add(lootEntry);
    }

    public String getDuration() {
        if (end == null) {
            return "running";
        }
        final Duration duration = Duration.between(start, end);
        long seconds = duration.getSeconds();
        final StringBuilder result = new StringBuilder();
        final int hours = (int) Math.floorDiv(seconds, SECONDS2HOURS);
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        seconds = Math.floorMod(seconds, SECONDS2HOURS);
        final int minutes = (int) Math.floorDiv(seconds, SECONDS2MINUTES);
        if (minutes > 0) {
            result.append(minutes).append("m ");
        }
        result.append(Math.floorMod(seconds, SECONDS2MINUTES)).append('s');
        return result.toString();
    }
}
