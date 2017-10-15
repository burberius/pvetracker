package net.troja.eve.pve.db.sites;

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

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SiteRepositoryIntegrationTest {
    @Autowired
    private SiteRepository classToTest;

    @Test
    public void saveAndFind() {
        final Site site = new Site();
        site.setName("Nix");
        site.setFaction(Faction.AMARR_EMPIRE);
        site.setType(SiteType.ANOMALY);

        classToTest.save(site);

        final Optional<Site> result = classToTest.findById(1);

        assertThat(result.isPresent(), equalTo(true));
    }
}
