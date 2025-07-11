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

import net.troja.eve.pve.PvEApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PriceRepositoryIntegrationTest {
    @Autowired
    private PriceRepository classToTest;

    @Test
    void deleteOld() {
        classToTest.save(new PriceBean(34, 123.45, LocalDateTime.now(PvEApplication.DEFAULT_ZONE)));
        classToTest.save(new PriceBean(35, 55.66, LocalDateTime.now(PvEApplication.DEFAULT_ZONE)
                .minusMinutes(20)));

        assertThat(classToTest.count(), equalTo(2L));

        classToTest.deleteByCreatedBefore(LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusMinutes(15));

        assertThat(classToTest.count(), equalTo(1L));
    }
}
