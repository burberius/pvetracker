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

import java.time.LocalDateTime;
import java.util.Optional;

import net.troja.eve.pve.PvEApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.db.solarsystem.SolarSystemRepository;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OutcomeRepositoryIntegrationTest {
    @Autowired
    private OutcomeRepository classToTest;

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private SiteRepository siteRepo;
    @Autowired
    private ShipRepository shipRepo;
    @Autowired
    private SolarSystemRepository systemRepo;

    @Test
    public void saveAndFind() {
        final Optional<SiteBean> site = siteRepo.findById(1);
        final Optional<AccountBean> account = accountRepo.findById(1);
        final Optional<SolarSystemBean> system = systemRepo.findById(30000142);

        final OutcomeBean outcome = new OutcomeBean();
        outcome.setAccount(account.get());
        final SiteBean siteEntry = site.get();
        outcome.setSite(siteEntry);
        outcome.setSiteName(siteEntry.getName());
        outcome.setSystem(system.get());
        ShipBean ship = new ShipBean("Test", "Gila", 123);
        ship = shipRepo.save(ship);
        outcome.setShip(ship);
        outcome.setStart(LocalDateTime.now(PvEApplication.DEFAULT_ZONE));

        final LootBean loot = new LootBean();
        loot.setCount(5);
        loot.setName("Loot");
        loot.setValue(4.2123);

        outcome.addLoot(loot);

        classToTest.save(outcome);

        final Optional<OutcomeBean> result = classToTest.findById(1L);

        assertThat(result.isPresent(), equalTo(true));

        final OutcomeBean out = result.get();
        assertThat(out.getLoot().size(), equalTo(1));
    }
}
