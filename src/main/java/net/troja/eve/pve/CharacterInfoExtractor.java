package net.troja.eve.pve;

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
