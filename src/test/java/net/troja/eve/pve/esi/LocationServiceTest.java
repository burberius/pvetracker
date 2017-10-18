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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.api.LocationApi;
import net.troja.eve.esi.auth.OAuth;
import net.troja.eve.esi.model.CharacterLocationResponse;
import net.troja.eve.esi.model.CharacterShipResponse;
import net.troja.eve.pve.db.account.Account;
import net.troja.eve.pve.db.solarsystem.SolarSystem;
import net.troja.eve.pve.db.solarsystem.SolarSystemRepository;
import net.troja.eve.pve.db.type.TypeTranslation;
import net.troja.eve.pve.db.type.TypeTranslationRespository;

public class LocationServiceTest {
    private static final int CHARACTER_ID = 12345;
    private static final String REFRESH_TOKEN = "54321";
    private static final int LOCATION_ID = 9999;
    private static final String LOCATION = "Jita";
    private static final String SHIP_NAME = "Alig";
    private static final String SHIP_TYPE = "Gila";
    private static final int SHIP_TYPE_ID = 666;

    private final LocationService classToTest = new LocationService();

    @Mock
    private LocationApi locationApi;
    @Mock
    private ResourceServerProperties resourceProperties;
    @Mock
    private SolarSystemRepository solarSystemRepo;
    @Mock
    private TypeTranslationRespository typeRepo;
    @Mock
    private OAuth auth;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setResourceProperties(resourceProperties);
        classToTest.init();
        classToTest.setApi(locationApi);
        classToTest.setSolarSystemRepository(solarSystemRepo);
        classToTest.setTypeRepository(typeRepo);
        classToTest.setAuth(auth);
    }

    @Test
    public void getLocation() throws ApiException {
        final CharacterLocationResponse fakeLocation = new CharacterLocationResponse();
        fakeLocation.setSolarSystemId(LOCATION_ID);
        when(locationApi.getCharactersCharacterIdLocation(CHARACTER_ID, AbstractEsiService.DATASOURCE, null, null, null)).thenReturn(fakeLocation);
        final SolarSystem solarSystem = new SolarSystem();
        solarSystem.setName(LOCATION);
        when(solarSystemRepo.findById(LOCATION_ID)).thenReturn(Optional.of(solarSystem));

        final String location = classToTest.getLocation(getAccount());

        assertThat(location, equalTo(LOCATION));
        verify(auth).setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    public void getLocationNotFound() throws ApiException {
        final CharacterLocationResponse fakeLocation = new CharacterLocationResponse();
        fakeLocation.setSolarSystemId(LOCATION_ID);
        when(locationApi.getCharactersCharacterIdLocation(CHARACTER_ID, AbstractEsiService.DATASOURCE, null, null, null)).thenReturn(fakeLocation);
        when(solarSystemRepo.findById(LOCATION_ID)).thenReturn(Optional.empty());

        final String location = classToTest.getLocation(getAccount());

        assertThat(location, equalTo("unknown"));
        verify(auth).setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    public void getLocationException() throws ApiException {
        final CharacterLocationResponse fakeLocation = new CharacterLocationResponse();
        fakeLocation.setSolarSystemId(LOCATION_ID);
        when(locationApi.getCharactersCharacterIdLocation(CHARACTER_ID, AbstractEsiService.DATASOURCE, null, null, null))
                .thenThrow(new ApiException());

        final String location = classToTest.getLocation(getAccount());

        assertThat(location, equalTo("unknown"));
        verify(auth).setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    public void getShip() throws ApiException {
        final CharacterShipResponse shipResponse = new CharacterShipResponse();
        shipResponse.setShipName(SHIP_NAME);
        shipResponse.setShipTypeId(SHIP_TYPE_ID);

        when(locationApi.getCharactersCharacterIdShip(CHARACTER_ID, AbstractEsiService.DATASOURCE, null, null, null)).thenReturn(shipResponse);
        final TypeTranslation typeTranslation = new TypeTranslation();
        typeTranslation.setName(SHIP_TYPE);
        when(typeRepo.findByTypeIdAndLanguage(SHIP_TYPE_ID, "en")).thenReturn(Optional.of(typeTranslation));

        final String ship = classToTest.getShip(getAccount());

        assertThat(ship, equalTo(SHIP_NAME + " (" + SHIP_TYPE + ")"));
        verify(auth).setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    public void getShipNotFound() throws ApiException {
        final CharacterShipResponse shipResponse = new CharacterShipResponse();
        shipResponse.setShipName(SHIP_NAME);
        shipResponse.setShipTypeId(SHIP_TYPE_ID);

        when(locationApi.getCharactersCharacterIdShip(CHARACTER_ID, AbstractEsiService.DATASOURCE, null, null, null)).thenReturn(shipResponse);
        when(typeRepo.findByTypeIdAndLanguage(SHIP_TYPE_ID, "en")).thenReturn(Optional.empty());

        final String ship = classToTest.getShip(getAccount());

        assertThat(ship, equalTo(SHIP_NAME));
        verify(auth).setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    public void getShipException() throws ApiException {
        final CharacterShipResponse shipResponse = new CharacterShipResponse();
        shipResponse.setShipName(SHIP_NAME);
        shipResponse.setShipTypeId(SHIP_TYPE_ID);

        when(locationApi.getCharactersCharacterIdShip(CHARACTER_ID, AbstractEsiService.DATASOURCE, null, null, null)).thenThrow(new ApiException());

        final String ship = classToTest.getShip(getAccount());

        assertThat(ship, equalTo("unknown"));
        verify(auth).setRefreshToken(REFRESH_TOKEN);
    }

    private Account getAccount() {
        final Account account = new Account();
        account.setCharacterId(CHARACTER_ID);
        account.setRefreshToken(REFRESH_TOKEN);
        return account;
    }
}
