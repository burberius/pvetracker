package net.troja.eve.pve.db.sites;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SiteRepository extends CrudRepository<Site, Integer> {
    Optional<Site> findByName(String name);
}
