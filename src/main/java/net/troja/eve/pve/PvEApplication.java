package net.troja.eve.pve;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.TimeZone;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.Formatter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@SpringBootApplication
@EnableScheduling
public class PvEApplication {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public PvEApplication() {
        super();
    }

    public static void main(final String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(PvEApplication.class, args);
    }

    @Bean
    public AuthorizationCodeResourceDetails getAuthorizationCodeResourceDetails() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

    @Bean
    public Formatter<LocalDateTime> localDateFormatter() {
        return new Formatter<LocalDateTime>() {
            private final DateTimeFormatter formater = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("yyyy-MM-dd HH:mm:ss")
                    .toFormatter();

            @Override
            public LocalDateTime parse(final String text, final Locale locale) {
                return LocalDateTime.parse(text, formater);
            }

            @Override
            public String print(final LocalDateTime object, final Locale locale) {
                return formater.format(object);
            }
        };
    }
}
