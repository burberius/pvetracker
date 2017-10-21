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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.auth.OAuth;

public class AbstractEsiService {
    public static final String DATASOURCE = "tranquility";

    @Autowired
    protected ResourceServerProperties resourceProperties;

    private OAuth auth;

    private AbstractEsiService() {
        super();
    }

    protected void init(final ApiClient apiClient) {
        auth = (OAuth) apiClient.getAuthentication("evesso");
        auth.setClientId(resourceProperties.getClientId());
        auth.setClientSecret(resourceProperties.getClientSecret());
    }

    protected void switchRefreshToken(final String token) {
        auth.setRefreshToken(token);
    }

    void setResourceProperties(final ResourceServerProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    void setAuth(final OAuth auth) {
        this.auth = auth;
    }
}
