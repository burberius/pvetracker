package net.troja.eve.pve.db.price;

import java.time.LocalDateTime;

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
public class PriceRepositoryIntegrationTest {
    @Autowired
    private PriceRepository classToTest;

    @Test
    public void deleteOld() {
        classToTest.save(new PriceBean(34, 123.45, LocalDateTime.now()));
        classToTest.save(new PriceBean(35, 55.66, LocalDateTime.now().minusMinutes(20)));

        assertThat(classToTest.count(), equalTo(2L));

        classToTest.deleteByCreatedBefore(LocalDateTime.now().minusMinutes(15));

        assertThat(classToTest.count(), equalTo(1L));
    }
}
