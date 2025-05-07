package net.troja.eve.pve.db;

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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
    public LocalDateTimeConverter() {
        super();
    }

    @Override
    public Timestamp convertToDatabaseColumn(final LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(Timestamp::valueOf).orElse(null);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(final Timestamp timestamp) {
        return Optional.ofNullable(timestamp).map(Timestamp::toLocalDateTime).orElse(null);
    }
}
