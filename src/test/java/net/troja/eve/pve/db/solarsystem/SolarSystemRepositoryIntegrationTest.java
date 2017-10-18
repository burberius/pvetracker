package net.troja.eve.pve.db.solarsystem;

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
public class SolarSystemRepositoryIntegrationTest {
    @Autowired
    private SolarSystemRepository classToTest;

    @Test
    public void findById() {
        final Optional<SolarSystem> system = classToTest.findById(30000142);
        assertThat(system.get().getName(), equalTo("Jita"));
    }
}
