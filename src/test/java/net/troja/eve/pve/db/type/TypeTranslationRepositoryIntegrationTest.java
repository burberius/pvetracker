package net.troja.eve.pve.db.type;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TypeTranslationRepositoryIntegrationTest {
    private static final int TYPE_ID = 123;
    @Autowired
    public TypeTranslationRepository classToTest;

    @Test
    void integrity() {
        assertThat(classToTest).isNotNull();

        classToTest.save(new TypeTranslationBean(TYPE_ID, "de", "Name DE", null));
        classToTest.save(new TypeTranslationBean(TYPE_ID, "en", "Name EN", null));

        assertThat(classToTest.findByTypeId(TYPE_ID)).hasSize(2);
    }
}