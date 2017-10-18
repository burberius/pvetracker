package net.troja.eve.pve.esi;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.pve.db.account.Account;
import net.troja.eve.pve.db.solarsystem.SolarSystem;
import net.troja.eve.pve.db.solarsystem.SolarSystemRepository;
import net.troja.eve.pve.db.type.TypeTranslation;
import net.troja.eve.pve.db.type.TypeTranslationRespository;

@Service
public class LocationService extends AbstractEsiService {
    private static final Logger LOGGER = LogManager.getLogger(LocationService.class);

    @Autowired
    private SolarSystemRepository solarSystemRepository;
    @Autowired
    private TypeTranslationRespository typeRepository;

    private LocationApi api = new LocationApi();

    public LocationService() {
        super();
    }

    @PostConstruct
    public void init() {
        super.init(api.getApiClient());
    }

    public String getLocation(final Account account) {
        switchRefreshToken(account.getRefreshToken());
        String location = "unknown";
        try {
            final CharacterLocationResponse locationResponse = api.getCharactersCharacterIdLocation(account.getCharacterId(), DATASOURCE, null,
                    null, null);
            final Optional<SolarSystem> solarSystem = solarSystemRepository.findById(locationResponse.getSolarSystemId());
            if (solarSystem.isPresent()) {
                location = solarSystem.get().getName();
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not get location of character {}", account.getCharacterName(), e);
        }
        return location;
    }

    public String getShip(final Account account) {
        switchRefreshToken(account.getRefreshToken());
        String name = "unknown";
        try {
            final CharacterShipResponse shipResponse = api.getCharactersCharacterIdShip(account.getCharacterId(), DATASOURCE, null, null, null);
            final StringBuilder nameConcat = new StringBuilder().append(shipResponse.getShipName());
            final Integer shipTypeId = shipResponse.getShipTypeId();
            final Optional<TypeTranslation> type = typeRepository.findByTypeIdAndLanguage(shipTypeId, "en");
            if (type.isPresent()) {
                nameConcat.append(" (").append(type.get().getName()).append(')');
            }
            name = nameConcat.toString();
        } catch (final ApiException e) {
            LOGGER.warn("Could not get ship of character {}", account.getCharacterName(), e);
        }
        return name;
    }

    void setApi(final LocationApi api) {
        this.api = api;
    }

    void setSolarSystemRepository(final SolarSystemRepository solarSystemRepository) {
        this.solarSystemRepository = solarSystemRepository;
    }

    void setTypeRepository(final TypeTranslationRespository typeRepository) {
        this.typeRepository = typeRepository;
    }
}
