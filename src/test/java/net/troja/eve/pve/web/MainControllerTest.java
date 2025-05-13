package net.troja.eve.pve.web;

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

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MainControllerTest {
    private final MainController classToTest = new MainController();

    @Test
    void indexNormal() {
        final String result = classToTest.index(null);

        assertThat(result, equalTo("index"));
    }

    @Test
    void indexAuthenticated() {
        final String result = classToTest.index(new TestingAuthenticationToken(null, null));

        assertThat(result, equalTo("redirect:/site"));
    }
}
