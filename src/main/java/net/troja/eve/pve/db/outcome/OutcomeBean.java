package net.troja.eve.pve.db.outcome;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.sites.SiteBean;

@Data
@NoArgsConstructor
@Entity
@Table(name = "outcome")
public class OutcomeBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountBean account;
    private String system;
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private ShipBean ship;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteBean site;
    private String siteName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date start = new Date();
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    private boolean faction;
    private boolean escalation;
    private double bountyValue;
    private double lootValue;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "outcome_id")
    private List<LootBean> loot = new ArrayList<>();

    public OutcomeBean(final AccountBean account, final String system, final ShipBean ship, final String siteName, final SiteBean site) {
        super();
        this.account = account;
        this.system = system;
        this.ship = ship;
        this.siteName = siteName;
        this.site = site;
    }

    public void addLoot(final LootBean lootEntry) {
        loot.add(lootEntry);
    }
}
