package net.troja.eve.pve.db.price;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PriceRepository extends CrudRepository<PriceBean, Integer> {
    @Modifying
    @Transactional
    void deleteByCreatedBefore(Date expiryDate);
}