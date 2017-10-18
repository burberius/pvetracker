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

import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import net.troja.eve.pve.db.account.Account;
import net.troja.eve.pve.db.account.AccountRepository;
import net.troja.eve.pve.db.sites.Site;
import net.troja.eve.pve.db.sites.SiteRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OutcomeRepositoryIntegrationTest {
    @Autowired
    private OutcomeRepository classToTest;

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private SiteRepository siteRepo;

    @Test
    public void saveAndFind() {
        final Optional<Site> site = siteRepo.findById(1);
        final Optional<Account> account = accountRepo.findById(1);

        final Outcome outcome = new Outcome();
        outcome.setAccount(account.get());
        final Site siteEntry = site.get();
        outcome.setSite(siteEntry);
        outcome.setSiteName(siteEntry.getName());
        outcome.setSystem("Nix");
        outcome.setShip("Gila");
        outcome.setStart(new Date());

        final Loot loot = new Loot();
        loot.setCount(5);
        loot.setName("Loot");
        loot.setValue(4.2123);

        outcome.addLoot(loot);

        classToTest.save(outcome);

        final Optional<Outcome> result = classToTest.findById(1L);

        assertThat(result.isPresent(), equalTo(true));

        final Outcome out = result.get();
        assertThat(out.getLoot().size(), equalTo(1));
    }
}
