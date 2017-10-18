package net.troja.eve.pve.db.type;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TypeTranslationRespository extends CrudRepository<TypeTranslation, Integer> {
    List<TypeTranslation> findByName(String name);

    Optional<TypeTranslation> findByTypeIdAndLanguage(Integer typeId, String language);
}
