package net.troja.eve.pve.web;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.sso.EveOAuth2User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;

public class ControllerHelper {
    public static AccountBean getAccount(Principal principal)  {
        return ((EveOAuth2User) ((OAuth2AuthenticationToken) principal).getPrincipal()).getAccount();
    }
}
