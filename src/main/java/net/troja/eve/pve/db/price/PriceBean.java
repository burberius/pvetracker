package net.troja.eve.pve.db.price;

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

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.troja.eve.pve.PvEApplication;

@Entity
@Getter
@Setter
@Table(name = "price")
public class PriceBean {
    @Id
    private int typeId;
    @Column(name = "money")
    private double value;
    private LocalDateTime created = LocalDateTime.now(PvEApplication.DEFAULT_ZONE);

    public PriceBean() {
        super();
    }

    public PriceBean(final int typeId, final double value, final LocalDateTime created) {
        super();
        this.typeId = typeId;
        this.value = value;
        this.created = created;
    }

    public PriceBean(final int typeId, final double value) {
        super();
        this.typeId = typeId;
        this.value = value;
    }
}
