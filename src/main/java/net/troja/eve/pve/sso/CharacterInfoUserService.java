package net.troja.eve.pve.sso;

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

import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public final class CharacterInfoUserService extends DefaultOAuth2UserService {
    private static final Logger LOGGER = LogManager.getLogger(CharacterInfoUserService.class);

    private final AccountRepository accountRepository;
    private final NimbusJwtDecoder jwtDecoder;

    public CharacterInfoUserService(final AccountRepository accountRepository, final NimbusJwtDecoder jwtDecoder) {
        this.accountRepository = accountRepository;
        this.jwtDecoder = jwtDecoder;
    }

    public AccountBean extractAccount(final Map<String, Object> map) {
        final int characterId = Integer.parseInt(((String) map.get("sub")).substring("CHARACTER:EVE:".length()));
        AccountBean account;
        final Optional<AccountBean> accountSearch = accountRepository.findById(characterId);
        if (accountSearch.isPresent()) {
            account = accountSearch.get();
            account.setLastLogin(LocalDateTime.now(PvEApplication.DEFAULT_ZONE));
            LOGGER.info("Updated account {}", account);
        } else {
            account = new AccountBean();
            account.setCharacterId(characterId);
            account.setCharacterName((String) map.get("name"));
            account.setCharacterOwnerHash((String) map.get("owner"));
            LOGGER.info("Saved new account {}", account);
        }
        accountRepository.save(account);
        LOGGER.info("Current count {}", accountRepository.count());

        return account;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Jwt decode = jwtDecoder.decode(userRequest.getAccessToken().getTokenValue());
        AccountBean account = extractAccount(decode.getClaims());
        return new EveOAuth2User(account);
    }
}
