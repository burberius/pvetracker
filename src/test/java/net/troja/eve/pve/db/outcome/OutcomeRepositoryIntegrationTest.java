package net.troja.eve.pve.db.outcome;

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
        final Optional<Account> account = accountRepo.findById(1L);

        final Outcome outcome = new Outcome();
        outcome.setAccount(account.get());
        outcome.setSite(site.get());
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
