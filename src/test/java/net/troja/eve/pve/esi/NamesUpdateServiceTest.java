package net.troja.eve.pve.esi;

import net.troja.eve.pve.db.type.TypeTranslationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class NamesUpdateServiceTest {

    @Autowired
    private TypeTranslationRepository typeTranslationRepository;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private NamesUpdateService classToTest = new NamesUpdateService();

    @BeforeEach
    public void setUp() {
        classToTest.setTypeTranslationRepository(typeTranslationRepository);
        classToTest.setTaskExecutor(taskExecutor);
        classToTest.init();
    }

    @Test
    public void injectedComponentsAreNotNull(){
        assertNotNull(typeTranslationRepository);
    }

    @Test
    @Disabled
    public void update() throws InterruptedException {
        classToTest.update();
        Thread.sleep(60000);

        assertThat(typeTranslationRepository.count()).isEqualTo(1040);
    }
}