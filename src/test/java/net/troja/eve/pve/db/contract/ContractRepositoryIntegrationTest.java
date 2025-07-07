package net.troja.eve.pve.db.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ContractRepositoryIntegrationTest {
    public static final int PRICE = 10_000;
    @Autowired
    private ContractRepository classToTest;

    @Test
    void addAndGet() {
        ContractBean contractBean = new ContractBean(123, 456, PRICE, OffsetDateTime.now(UTC));
        classToTest.save(contractBean);

        Optional<ContractBean> entry = classToTest.findById(123);
        assertThat(entry).isPresent();
        assertThat(entry.get().getPrice()).isEqualTo(PRICE);
    }

    @Test
    void deleteOutdated() {
        ContractBean contractBean = new ContractBean(123, 456, PRICE, OffsetDateTime.now(UTC).minusMinutes(1));
        classToTest.save(contractBean);

        assertThat(classToTest.count()).isEqualTo(1);

        classToTest.deleteByDateExpiredBefore(OffsetDateTime.now(UTC));

        assertThat(classToTest.count()).isEqualTo(0);
    }

    @Test
    void getLowestPriceAndDistinctByTypeId() {
        classToTest.deleteAll();
        ContractBean contractBean1 = new ContractBean(1234, 456, PRICE, OffsetDateTime.now(UTC));
        classToTest.save(contractBean1);
        ContractBean contractBean2 = new ContractBean(1235, 456, 12_345, OffsetDateTime.now(UTC));
        classToTest.save(contractBean2);
        ContractBean contractBean3 = new ContractBean(12353, 4567, 22_222, OffsetDateTime.now(UTC));
        classToTest.save(contractBean3);

        Optional<Double> price = classToTest.findLowestPriceByTypeId(456);
        assertThat(price).isPresent().hasValue((double)PRICE);

        assertThat(classToTest.countTypeIds()).isEqualTo(2);

        assertThat(classToTest.getAllContractIds()).containsExactly(1234, 1235, 12353);
    }
}