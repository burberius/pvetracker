package net.troja.eve.pve.db.solarsystem;

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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "solar_system")
public class SolarSystemBean {
    private static final double SEC_LOW = 0.4;
    @Id
    private int id;
    private String name;
    private Double security;

    public SolarSystemBean() {
        super();
    }

    public String getSecClass() {
        String cssClass;
        final double sec = Math.round(security * 10) / 10D;
        if (sec <= 0) {
            cssClass = "sec-0";
        } else if (sec <= SEC_LOW) {
            cssClass = "sec-low";
        } else {
            cssClass = "sec-" + Double.toString(sec).replaceAll("\\.", "");
        }
        return cssClass.replace("10", "1");
    }
}
