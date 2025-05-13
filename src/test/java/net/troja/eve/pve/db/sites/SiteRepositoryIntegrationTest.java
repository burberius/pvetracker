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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SiteRepositoryIntegrationTest {
    @Autowired
    private SiteRepository classToTest;

    @Test
    void saveAndFind() {
        final Optional<SiteBean> result = classToTest.findById(1);

        assertThat(result).isPresent();
    }

    @Test
    void searchCaseInsensitive() {
        final Optional<List<SiteBean>> result = classToTest.findByNameContainingIgnoreCase("angel");

        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(100);
        result.get().forEach(site -> assertThat(site.getName()).contains("Angel"));
    }
}
