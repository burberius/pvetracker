package net.troja.eve.pve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@SpringBootApplication
public class PvEApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PvEApplication.class, args);
    }

    @Bean
    public AuthorizationCodeResourceDetails getAuthorizationCodeResourceDetails() {
        return new AuthorizationCodeResourceDetails();
    }
}
