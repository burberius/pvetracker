package net.troja.eve.pve.db.outcome;

import java.time.LocalDateTime;
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

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;
import net.troja.eve.pve.db.sites.SiteBean;
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
    @Autowired
    private ShipRepository shipRepo;

    @Test
    public void saveAndFind() {
        final Optional<SiteBean> site = siteRepo.findById(1);
        final Optional<AccountBean> account = accountRepo.findById(1);

        final OutcomeBean outcome = new OutcomeBean();
        outcome.setAccount(account.get());
        final SiteBean siteEntry = site.get();
        outcome.setSite(siteEntry);
        outcome.setSiteName(siteEntry.getName());
        outcome.setSystem("Nix");
        ShipBean ship = new ShipBean("Test", "Gila", 123);
        ship = shipRepo.save(ship);
        outcome.setShip(ship);
        outcome.setStart(LocalDateTime.now());

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
