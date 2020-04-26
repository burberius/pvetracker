package net.troja.eve.pve.sso;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.account.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

public class DelegatingClientService implements OAuth2AuthorizedClientService {

    private OAuth2AuthorizedClientService delegate;
    private AccountRepository accountRepository;

    public DelegatingClientService(OAuth2AuthorizedClientService delegate, AccountRepository accountRepository) {
        this.delegate = delegate;
        this.accountRepository = accountRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return delegate.loadAuthorizedClient(clientRegistrationId, principalName);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        delegate.saveAuthorizedClient(authorizedClient, principal);
        AccountBean account = ((EveOAuth2User) principal.getPrincipal()).getAccount();
        account.setRefreshToken(authorizedClient.getRefreshToken().getTokenValue());
        accountRepository.save(account);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        delegate.removeAuthorizedClient(clientRegistrationId, principalName);
    }
}
