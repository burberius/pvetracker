package net.troja.eve.pve.db.contract;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends CrudRepository<ContractBean, Integer> {
    @Transactional
    void deleteByDateExpiredBefore(OffsetDateTime expiryDate);

    @Query(value = "select min(c.price) from ContractBean c where c.typeId = :typeId")
    Optional<Double> findLowestPriceByTypeId(int typeId);

    @NativeQuery(value = "select count(distinct c.type_id) from contracts c")
    int countTypeIds();

    @Query(value = "select c.contractId from ContractBean c")
    List<Integer> getAllContractIds();
}
