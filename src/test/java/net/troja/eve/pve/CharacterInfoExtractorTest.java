package net.troja.eve.pve;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;

public class CharacterInfoExtractorTest {
    private static final int CHARACTER_ID = 12345;
    private static final String NAME = "Noname";
    private static final String HASH = "Hash";
    private static final String TOKEN = "Token";

    private CharacterInfoExtractor classToTest;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OAuth2RestTemplate restTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest = new CharacterInfoExtractor(accountRepository, restTemplate);
    }

    @Test
    public void extractPrincipalNotFound() {

        final Map<String, Object> map = getCharacterMap();

        when(accountRepository.findById(CHARACTER_ID)).thenReturn(Optional.empty());
        when(restTemplate.getAccessToken()).thenReturn(getAccessToken());

        final AccountBean account = (AccountBean) classToTest.extractPrincipal(map);

        verify(accountRepository).save(account);

        assertEqual(account, getExpectedAccount());
    }

    private OAuth2AccessToken getAccessToken() {
        final DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("");
        accessToken.setRefreshToken(new DefaultOAuth2RefreshToken(TOKEN));
        return accessToken;
    }

    @Test
    public void extractPrincipalFound() {
        final Map<String, Object> map = getCharacterMap();
        final LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        final AccountBean dbAccount = getExpectedAccount();
        dbAccount.setLastLogin(LocalDateTime.now().minusDays(2));
        dbAccount.setCreated(LocalDateTime.now().minusDays(5));
        dbAccount.setRefreshToken(HASH);
        when(accountRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(dbAccount));
        when(restTemplate.getAccessToken()).thenReturn(getAccessToken());

        final AccountBean account = (AccountBean) classToTest.extractPrincipal(map);

        verify(accountRepository).save(account);

        assertEqual(account, getExpectedAccount());
        assertThat(account.getLastLogin().isAfter(lastLogin), equalTo(true));
        assertThat(account.getCreated(), equalTo(dbAccount.getCreated()));
        assertThat(account.getRefreshToken(), equalTo(dbAccount.getRefreshToken()));
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
