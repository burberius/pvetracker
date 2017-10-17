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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import net.troja.eve.esi.auth.SsoScopes;
import net.troja.eve.pve.db.account.AccountRepository;

@Configuration
@EnableOAuth2Client
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private OAuth2ClientContext oauth2ClientContext;
    @Autowired
    private AuthorizationCodeResourceDetails oauth2Client;
    @Autowired
    private ResourceServerProperties resourceProperties;
    @Autowired
    private AccountRepository accountRepository;

    private static final List<String> SCOPES = new ArrayList<>();

    public SecurityConfiguration() {
        SCOPES.add(SsoScopes.ESI_LOCATION_READ_LOCATION_V1);
        SCOPES.add(SsoScopes.ESI_LOCATION_READ_ONLINE_V1);
        SCOPES.add(SsoScopes.ESI_LOCATION_READ_SHIP_TYPE_V1);
        SCOPES.add(SsoScopes.ESI_WALLET_READ_CHARACTER_WALLET_V1);
        SCOPES.add(SsoScopes.ESI_UI_OPEN_WINDOW_V1);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**", "/css/**", "/images/**").permitAll().anyRequest()
                .authenticated().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").permitAll().and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    private Filter ssoFilter() {
        final OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter("/login");
        oauth2Client.setScope(SCOPES);
        final OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(oauth2Client, oauth2ClientContext);
        filter.setRestTemplate(restTemplate);
        final UserInfoTokenServices tokenServices = new UserInfoTokenServices(resourceProperties.getUserInfoUri(), oauth2Client.getClientId());
        tokenServices.setRestTemplate(restTemplate);
        tokenServices.setPrincipalExtractor(new CharacterInfoExtractor(accountRepository, restTemplate));
        filter.setTokenServices(tokenServices);
        return filter;
    }
}
