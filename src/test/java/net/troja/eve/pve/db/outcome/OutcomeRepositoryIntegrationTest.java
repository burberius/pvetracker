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
import java.util.List;
import java.util.Optional;

import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.stats.MonthOverviewStatBean;
import net.troja.eve.pve.db.stats.SiteCountStatBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.db.solarsystem.SolarSystemRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class OutcomeRepositoryIntegrationTest {
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

    private AccountBean accountBean;

    @BeforeEach
    void setUp() {
        accountBean = accountRepo.findById(1).get();
        if(classToTest.count() == 0) {
            createOutcome();
        }
    }

    @Test
    void saveAndFind() {
        final Optional<OutcomeBean> result = classToTest.findById(1L);

        assertThat(result.isPresent()).isEqualTo(true);

        final OutcomeBean out = result.get();
        assertThat(out.getLoot()).hasSize(1);
    }

    @Test
    void getMonthlyOverviewStats() {
        List<MonthOverviewStatBean> monthlyOverviewStats =
                classToTest.getMonthlyOverviewStats(accountBean, LocalDateTime.now().minusHours(10));

        assertThat(monthlyOverviewStats).hasSize(1);
    }

    @Test
    void getSiteCountStats() {
        final Optional<OutcomeBean> result = classToTest.findById(1L);
        System.out.println(result.get());

        List<SiteCountStatBean> siteCountStats = classToTest.getSiteCountStats(accountBean, PageRequest.of(0, 16));

        assertThat(siteCountStats).hasSize(1);
    }

    @Test
    void findByAccountOrderByStartTimeDesc() {
        final List<OutcomeBean> result = classToTest.findByAccountOrderByStartTimeDesc(accountBean);

        assertThat(result).hasSize(1);
    }

    private void createOutcome() {
        final Optional<SiteBean> site = siteRepo.findById(1);
        final Optional<SolarSystemBean> system = systemRepo.findById(30000142);

        final OutcomeBean outcome = new OutcomeBean();
        outcome.setAccount(accountBean);
        final SiteBean siteEntry = site.get();
        outcome.setSite(siteEntry);
        outcome.setSiteName(siteEntry.getName());
        outcome.setSystem(system.get());
        ShipBean ship = new ShipBean("Test", "Gila", 123);
        ship = shipRepo.save(ship);
        outcome.setShip(ship);
        outcome.setStartTime(LocalDateTime.now(PvEApplication.DEFAULT_ZONE));
        outcome.setEscalation(false);
        outcome.setFaction(false);

        final LootBean loot = new LootBean();
        loot.setCount(5);
        loot.setName("Loot");
        loot.setValue(4.2123);

        outcome.addLoot(loot);

        classToTest.save(outcome);
    }
}
