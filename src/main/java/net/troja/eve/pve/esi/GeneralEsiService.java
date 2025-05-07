package net.troja.eve.pve.esi;

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

import lombok.Setter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.auth.OAuth;
import org.springframework.beans.factory.annotation.Value;

public class GeneralEsiService {
    public static final String DATASOURCE = "tranquility";

    @Setter
    @Value("${spring.security.oauth2.client.registration.eve.client-id}")
    protected String clientId;

    private OAuth auth;

    protected void init(final ApiClient apiClient) {
        auth = (OAuth) apiClient.getAuthentication("evesso");
        auth.setClientId(clientId);
    }

    protected void switchRefreshToken(final String token) {
        auth.setAuth(clientId, token);
    }

    void setAuth(final OAuth auth) {
        this.auth = auth;
    }
}
