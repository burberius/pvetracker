package net.troja.eve.pve.esi;

import net.troja.eve.pve.db.type.TypeTranslationRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.assertNotNull;

@DataJpaTest
public class NamesUpdateServiceTest {

    @Autowired
    private TypeTranslationRepository typeTranslationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private NamesUpdateService classToTest = new NamesUpdateService();

    @Before
    public void setUp() {
        classToTest.setJdbcTemplate(jdbcTemplate);
        classToTest.setTypeTranslationRepository(typeTranslationRepository);
    }

    @Test
    public void injectedComponentsAreNotNull(){
        assertNotNull(jdbcTemplate);
        assertNotNull(typeTranslationRepository);
    }

    @Test
    public void update() throws InterruptedException {
        classToTest.update();
        Thread.sleep(60000);
    }
}