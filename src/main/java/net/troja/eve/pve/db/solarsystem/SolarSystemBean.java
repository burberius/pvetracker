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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "solar_system")
public class SolarSystemBean {
    @Id
    private int id;
    private String name;
    private Double security;

    public SolarSystemBean() {
        super();
    }

    public String getSecClass() {
        final double sec = Math.round(security * 10) / 10D;
        if (sec <= 0) {
            return "sec-0";
        } else if (sec <= 0.4) {
            return "sec-low";
        } else if (sec <= 0.5) {
            return "sec-05";
        } else if (sec <= 0.6) {
            return "sec-06";
        } else if (sec <= 0.7) {
            return "sec-07";
        } else if (sec <= 0.8) {
            return "sec-08";
        } else if (sec <= 0.9) {
            return "sec-09";
        }
        return "sec-1";
    }
}
