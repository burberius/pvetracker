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

import net.troja.eve.esi.api.LocationApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.Formatter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class PvEApplication {
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");

    public PvEApplication() {
        super();
    }

    public static void main(final String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(PvEApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("EveTypesLookup-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Formatter<LocalDateTime> localDateFormatter() {
        return new Formatter<>() {
            private final DateTimeFormatter formater = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .toFormatter(Locale.GERMAN);

            @NotNull
            @Override
            public LocalDateTime parse(@NotNull final String text, @NotNull final Locale locale) {
                return LocalDateTime.parse(text, formater);
            }

            @NotNull
            @Override
            public String print(@NotNull final LocalDateTime object, @NotNull final Locale locale) {
                return formater.format(object);
            }
        };
    }

    @Bean
    public LocationApi getLocationApi() {
        return new LocationApi();
    }
}