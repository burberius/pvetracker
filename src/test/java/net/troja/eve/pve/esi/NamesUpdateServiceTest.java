package net.troja.eve.pve.esi;

import net.troja.eve.pve.db.type.TypeTranslationRepository;
import net.troja.eve.pve.discord.DiscordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class NamesUpdateServiceTest {

    @Autowired
    private TypeTranslationRepository typeTranslationRepository;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Mock
    private DiscordService discordService;

    private NamesUpdateService classToTest;

    @BeforeEach
    void setUp() {
        classToTest = new NamesUpdateService(typeTranslationRepository, taskExecutor, discordService);
        classToTest.init();
    }

    @Test
    void injectedComponentsAreNotNull(){
        assertNotNull(typeTranslationRepository);
    }

    @Test
    @Disabled("Takes too long and the amount regularly changes")
    void update() throws InterruptedException {
        classToTest.update();
        Thread.sleep(60000);

        assertThat(typeTranslationRepository.count()).isEqualTo(1040);
    }
}