package net.troja.eve.pve;

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
