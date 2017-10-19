package net.troja.eve.pve.web;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.ui.Model;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.esi.LocationService;

public class SitesControllerTest {
    private static final String SITE_NAME = "Angel Vigil";
    private static final String SYSTEM = "Reset";

    private final SitesController classToTest = new SitesController();

    @Mock
    private SiteRepository siteRepo;
    @Mock
    private OutcomeRepository outcomeRepo;
    @Mock
    private OAuth2Authentication principal;
    @Mock
    private Model model;
    @Mock
    private LocationService locationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        classToTest.setOutcomeRepo(outcomeRepo);
        classToTest.setSiteRepo(siteRepo);
        classToTest.setLocationService(locationService);
    }

    @Test
    public void sites() {
        final List<OutcomeBean> outcomes = new ArrayList<>();

        when(outcomeRepo.findByAccountOrderByStartDesc(any())).thenReturn(outcomes);

        final String result = classToTest.sites(null, model, principal);

        verify(model).addAttribute("outcomes", outcomes);

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

        final SiteBean site = new SiteBean();
        site.setName(SITE_NAME);
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setId(1);

        final AccountBean account = new AccountBean();

        when(siteRepo.findByName(SITE_NAME)).thenReturn(Optional.of(site));
        when(outcomeRepo.save(any())).thenReturn(outcome);
        when(principal.getPrincipal()).thenReturn(account);
        when(locationService.getLocation(account)).thenReturn(SYSTEM);

        when(locationService.getShip(account)).thenReturn(new ShipBean());

        final String result = classToTest.start(model, principal);

        assertThat(result, equalTo("redirect:/sites/1"));
        assertThat(model.isError(), equalTo(false));
    }
}
