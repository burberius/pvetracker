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

import net.troja.eve.pve.db.account.AccountRepository;
import net.troja.eve.pve.sso.CharacterInfoUserService;
import net.troja.eve.pve.sso.DelegatingClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OAuth2AuthorizedClientService clientService;

    public SecurityConfiguration() {
        super();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests(a -> a
                    .antMatchers("/", "/login**", "/js/**", "/css/**", "/images/**", "/favicon.ico", "/favicon.png", "/apple-touch-icon.png").permitAll()
                    .anyRequest().authenticated()
                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizedClientService(new DelegatingClientService(clientService, accountRepository))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(getUserService())
                        )
                );
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> getUserService() {
        return new CharacterInfoUserService(accountRepository);
    }
}
