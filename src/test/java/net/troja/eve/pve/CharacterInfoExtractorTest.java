package net.troja.eve.pve;

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
