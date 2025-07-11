package net.troja.eve.pve.esi;

/*
 * ====================================================
 * Eve Online PvE Tracker
 * ----------------------------------------------------
 * Copyright (C) 2017 Jens Oberender <j.obi@troja.net>
 * ----------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * ====================================================
 */

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.outcome.ShipRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.db.solarsystem.SolarSystemRepository;
import net.troja.eve.pve.db.type.TypeTranslationBean;
import net.troja.eve.pve.db.type.TypeTranslationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LocationService extends GeneralEsiService {
    private static final Logger LOGGER = LogManager.getLogger(LocationService.class);
    private static final Pattern SHIPNAME_MATCHER = Pattern.compile(".['|\"](.*)['|\"]");

    private final SolarSystemRepository solarSystemRepository;
    private final TypeTranslationRepository typeRepository;
    private final ShipRepository shipRepository;
    private final LocationApi api;

    @PostConstruct
    public void init() {
        super.init(api.getApiClient());
    }

    public SolarSystemBean getLocation(final AccountBean account) {
        switchRefreshToken(account.getRefreshToken());
        try {
            final CharacterLocationResponse locationResponse = api.getCharactersCharacterIdLocation(account.getCharacterId(), DATASOURCE, null,
                    null);
            final Optional<SolarSystemBean> solarSystem = solarSystemRepository.findById(locationResponse.getSolarSystemId());
            if (solarSystem.isPresent()) {
                return solarSystem.get();
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not get location of character {}", account.getCharacterName(), e);
        }
        return null;
    }

    public ShipBean getShip(final AccountBean account) {
        switchRefreshToken(account.getRefreshToken());
        ShipBean ship = null;
        try {
            final CharacterShipResponse shipResponse = api.getCharactersCharacterIdShip(account.getCharacterId(), DATASOURCE, null, null);
            final String shipName = cleanShipname(shipResponse.getShipName());
            final Integer shipTypeId = shipResponse.getShipTypeId();
            final Optional<ShipBean> shipOptional = shipRepository.findByNameAndTypeId(shipName, shipTypeId);

            if (shipOptional.isPresent()) {
                ship = shipOptional.get();
            } else {
                ship = createShip(shipName, shipTypeId);
                ship = shipRepository.save(ship);
            }
        } catch (final ApiException e) {
            LOGGER.warn("Could not get ship of character {}", account.getCharacterName(), e);
        }
        return ship;
    }

    protected String cleanShipname(String shipName) {
        Matcher matcher = SHIPNAME_MATCHER.matcher(shipName);
        if(matcher.matches()) {
            String result = matcher.group(1);
            result = result.replace("\\x", "%");
            return URLDecoder.decode(result, Charset.forName("ISO-8859-15"));
        }
        return shipName;
    }

    private ShipBean createShip(final String shipName, final Integer shipTypeId) {
        final Optional<TypeTranslationBean> type = typeRepository.findByTypeIdAndLanguage(shipTypeId, "en");
        String shipTypeName = "unknown";
        if (type.isPresent()) {
            shipTypeName = type.get().getName();
        }
        return new ShipBean(shipName, shipTypeName, shipTypeId);
    }
}
