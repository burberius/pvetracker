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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.account.Account;
import net.troja.eve.pve.account.AccountRepository;

public class CharacterInfoExtractorTest {
    private CharacterInfoExtractor classToTest;

    @Mock
    private AccountRepository accountRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest = new CharacterInfoExtractor(accountRepository);
    }

    @Test
    public void extractPrincipal() {
        final int characterId = 12345;
        final String name = "Noname";
        final String hash = "Hash";

        final Map<String, Object> map = new HashMap<>();
        map.put("CharacterID", characterId);
        map.put("CharacterName", name);
        map.put("CharacterOwnerHash", hash);

        when(accountRepository.findById((long) characterId)).thenReturn(Optional.empty());

        final Account account = (Account) classToTest.extractPrincipal(map);

        verify(accountRepository).save(account);
        assertThat(account.getCharacterId(), equalTo((long) characterId));
        assertThat(account.getCharacterName(), equalTo(name));
        assertThat(account.getCharacterOwnerHash(), equalTo(hash));
    }
}
