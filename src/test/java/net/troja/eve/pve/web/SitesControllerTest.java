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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.outcome.Outcome;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.sites.Site;
import net.troja.eve.pve.db.sites.SiteRepository;

public class SitesControllerTest {
    private static final String SITE_NAME = "Angel Vigil";

    private final SitesController classToTest = new SitesController();

    @Mock
    private SiteRepository siteRepo;
    @Mock
    private OutcomeRepository outcomeRepo;
    @Mock
    private OAuth2Authentication principal;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setOutcomeRepo(outcomeRepo);
        classToTest.setSiteRepo(siteRepo);
    }

    @Test
    public void sites() {
        final String result = classToTest.sites(null);

        assertThat(result, equalTo("sites"));
    }

    @Test
    public void startError() {
        final StartModel model = new StartModel();
        model.setName(" ");

        final String result = classToTest.start(model, principal);

        assertThat(result, equalTo("sites"));
        assertThat(model.isError(), equalTo(true));
    }

    @Test
    public void start() {
        final StartModel model = new StartModel();
        model.setName(SITE_NAME);

        final Site site = new Site();
        site.setName(SITE_NAME);
        final Outcome outcome = new Outcome();
        outcome.setId(1);

        when(siteRepo.findByName(SITE_NAME)).thenReturn(Optional.of(site));
        when(outcomeRepo.save(any())).thenReturn(outcome);

        final String result = classToTest.start(model, principal);

        assertThat(result, equalTo("redirect:/sites/1"));
        assertThat(model.isError(), equalTo(false));
    }
}
