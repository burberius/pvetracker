package net.troja.eve.pve.db.outcome;

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


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;

@Entity
@Getter
@Setter
@ToString
@Table(name = "loot")
public class LootBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int count = 1;
    private String name;
    private int typeId;
    @Column(name = "money")
    private double value;

    public LootBean() {
        super();
    }

    public LootBean(final int typeId, final String name, final int count) {
        super();
        this.count = count;
        this.name = name;
        this.typeId = typeId;
    }

    public LootBean(final int typeId, final String name, final int count, final double value) {
        super();
        this.count = count;
        this.name = name;
        this.typeId = typeId;
        this.value = value;
    }

    public String getValueString() {
        if (count == 1) {
            return String.format(Locale.GERMAN, "%,d", Math.round(value)) + " ISK";
        } else {
            return String.format(Locale.GERMAN, "%,d", Math.round(value * count)) + " ISK ("
                    + String.format(Locale.GERMAN, "%,d", Math.round(value)) + " ISK per unit)";
        }
    }
}
