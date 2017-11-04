package net.troja.eve.pve.web;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;

@Data
@EqualsAndHashCode(callSuper = true)
public class OutcomeModelBean extends OutcomeBean {
    private String lootContent;

    public OutcomeModelBean() {
        super();
    }

    public OutcomeModelBean(final AccountBean account, final SolarSystemBean system, final ShipBean ship, final String siteName,
            final SiteBean site) {
        super(account, system, ship, siteName, site);
    }

    public OutcomeModelBean(final OutcomeBean original) {
        super();
        setAccount(original.getAccount());
        setBountyValue(original.getBountyValue());
        setEnd(original.getEnd());
        setEscalation(original.isEscalation());
        setFaction(original.isFaction());
        setId(original.getId());
        setLoot(original.getLoot());
        setLootValue(original.getLootValue());
        setShip(original.getShip());
        setSite(original.getSite());
        setSiteName(original.getSiteName());
        setStart(original.getStart());
        setSystem(original.getSystem());
    }
}
