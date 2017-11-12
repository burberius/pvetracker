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
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;

public final class CharacterInfoExtractor implements PrincipalExtractor {
    private static final Logger LOGGER = LogManager.getLogger(CharacterInfoExtractor.class);

    private final AccountRepository accountRepository;
    private final OAuth2RestTemplate restTemplate;

    public CharacterInfoExtractor(final AccountRepository accountRepository, final OAuth2RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Object extractPrincipal(final Map<String, Object> map) {
        final Integer characterId = (Integer) map.get("CharacterID");
        AccountBean account = null;
        final OAuth2AccessToken accessToken = restTemplate.getAccessToken();
        final Optional<AccountBean> accountSearch = accountRepository.findById(characterId);
        if (accountSearch.isPresent()) {
            account = accountSearch.get();
            account.setLastLogin(LocalDateTime.now());
            LOGGER.info("Updated account " + account);
        } else {
            account = new AccountBean();
            account.setCharacterId(characterId);
            account.setCharacterName((String) map.get("CharacterName"));
            account.setCharacterOwnerHash((String) map.get("CharacterOwnerHash"));
            LOGGER.info("Saved new account " + account);
        }
        account.setRefreshToken(accessToken.getRefreshToken().getValue());
        accountRepository.save(account);
        LOGGER.info("Current count " + accountRepository.count());

        return account;
    }

}
