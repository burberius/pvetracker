package net.troja.eve.pve;

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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.troja.eve.pve.sso.CharacterInfoUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;

public class CharacterInfoExtractorTest {
    private static final int CHARACTER_ID = 12345;
    private static final String NAME = "Noname";
    private static final String HASH = "Hash";
    private static final String TOKEN = "Token";

    private CharacterInfoUserService classToTest;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OAuth2RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest = new CharacterInfoUserService(accountRepository);
    }

    @Test
    @Disabled
    public void extractPrincipalNotFound() {

        final Map<String, Object> map = getCharacterMap();

        when(accountRepository.findById(CHARACTER_ID)).thenReturn(Optional.empty());
        when(restTemplate.getAccessToken()).thenReturn(getAccessToken());

        /*final AccountBean account = (AccountBean) classToTest.extractAccount(map, userRequest.getAccessToken());

        verify(accountRepository).save(account);

        assertEqual(account, getExpectedAccount());*/
    }

    private OAuth2AccessToken getAccessToken() {
        final DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("");
        accessToken.setRefreshToken(new DefaultOAuth2RefreshToken(TOKEN));
        return accessToken;
    }

    @Test
    @Disabled
    public void extractPrincipalFound() {
        final Map<String, Object> map = getCharacterMap();
        final LocalDateTime lastLogin = LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusDays(1);

        final AccountBean dbAccount = getExpectedAccount();
        dbAccount.setLastLogin(LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusDays(2));
        dbAccount.setCreated(LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusDays(5));
        dbAccount.setRefreshToken(HASH);
        when(accountRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(dbAccount));
        when(restTemplate.getAccessToken()).thenReturn(getAccessToken());
    }

    private void assertEqual(final AccountBean actual, final AccountBean expected) {
        assertThat(actual.getCharacterId(), equalTo(expected.getCharacterId()));
        assertThat(actual.getCharacterName(), equalTo(expected.getCharacterName()));
        assertThat(actual.getCharacterOwnerHash(), equalTo(expected.getCharacterOwnerHash()));
        assertThat(actual.getRefreshToken(), equalTo(expected.getRefreshToken()));
    }

    private Map<String, Object> getCharacterMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("CharacterID", CHARACTER_ID);
        map.put("CharacterName", NAME);
        map.put("CharacterOwnerHash", HASH);
        return map;
    }

    private AccountBean getExpectedAccount() {
        final AccountBean account = new AccountBean();
        account.setCharacterId(CHARACTER_ID);
        account.setCharacterName(NAME);
        account.setCharacterOwnerHash(HASH);
        account.setRefreshToken(TOKEN);
        return account;
    }
}
