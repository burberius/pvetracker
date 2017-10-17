package net.troja.eve.pve.db.sites;

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

public enum Faction {
    AMARR_EMPIRE("Amarr Empire"),
    AMMATAR_MANDATE("Ammatar Mandate"),
    KHANID_KINGDOM("Khanid Kingdom"),
    BLOOD_RAIDERS("Blood Raiders"),
    SANSHAS_NATION("Sansha's Nation"),
    CALDARI_STATE("Caldari State"),
    GURISTAS_PIRATES("Guristas Pirates"),
    MORDUS_LEGION("Mordu's Legion"),
    GALLENTE_FEDERATION("Gallente Federation"),
    INTAKI_SYNDICATE("Intaki Syndicate"),
    O_R_E("O.R.E."),
    SERPENTIS("Serpentis"),
    MINMATAR_REPUBLIC("Minmatar Republic"),
    ANGEL_CARTEL("Angel Cartel"),
    THUKKER_TRIBE("Thukker Tribe"),
    CONCORD_ASSEMBLY("Concord Assembly"),
    INTERBUS("Interbus"),
    SISTERS_OF_EVE("Sisters of EVE");

    private String name;

    Faction(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
