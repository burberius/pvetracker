package net.troja.eve.pve;

/*-
 * ========================================================================
 * Eve Online PvE Tracker
 * ------------------------------------------------------------------------
 * Copyright (C) 2017 Jens Oberender <j.obi@troja.net>
 * ------------------------------------------------------------------------
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
 * ========================================================================
 */

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import net.troja.eve.pve.account.Account;
import net.troja.eve.pve.account.AccountRepository;

public class CharacterInfoExtractor implements PrincipalExtractor {
    private static final Logger LOGGER = LogManager.getLogger(CharacterInfoExtractor.class);

    private final AccountRepository accountRepository;

    public CharacterInfoExtractor(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Object extractPrincipal(final Map<String, Object> map) {
        final Integer characterId = (Integer) map.get("CharacterID");
        Account account = null;
        final Optional<Account> accountSearch = accountRepository.findById(characterId.longValue());
        if (!accountSearch.isPresent()) {
            account = new Account();
            account.setCharacterId(characterId);
            account.setCharacterName((String) map.get("CharacterName"));
            account.setCharacterOwnerHash((String) map.get("CharacterOwnerHash"));
            accountRepository.save(account);
            LOGGER.info("Saved new account " + account);
        } else {
            account = accountSearch.get();
            account.setLastLogin(new Date());
            accountRepository.save(account);
        }
        LOGGER.info("Current count " + accountRepository.count());

        return account;
    }

}
