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

import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.content.ContentParserService;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.discord.DiscordService;
import net.troja.eve.pve.esi.LocationService;
import net.troja.eve.pve.sso.EveOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SitesControllerTest {
    private static final String SITE_NAME = "Angel Vigil";
    private static final int CHARACTER_ID = 1234;
    private static final long OUTCOME_ID = 5;
    private static final int BOUNTY = 1234;
    private static final int REWARD = 4321;
    private static final String LOOT = "Loot";

    private final SitesController classToTest = new SitesController();

    private final AccountBean account = new AccountBean();

    @Mock
    private SiteRepository siteRepo;
    @Mock
    private OutcomeRepository outcomeRepo;
    @Mock
    private OAuth2AuthenticationToken principal;
    @Mock
    private Model model;
    @Mock
    private LocationService locationService;
    @Mock
    private ContentParserService contentParserService;
    @Mock
    private DiscordService discordService;

    @BeforeEach
    public void setUp() {
        account.setCharacterId(CHARACTER_ID);
        classToTest.setOutcomeRepo(outcomeRepo);
        classToTest.setSiteRepo(siteRepo);
        classToTest.setLocationService(locationService);
        classToTest.setContentParserService(contentParserService);
        classToTest.setDiscordService(discordService);
    }

    @Test
    public void search() {
        final String query = "123";
        final SiteBean site = new SiteBean();
        when(siteRepo.findByNameContaining(query)).thenReturn(Optional.of(Arrays.asList(site)));

        final List<String> result = classToTest.search(query);

        assertThat(result.size(), equalTo(1));
    }

    @Test
    public void searchNotFound() {
        final String query = "123";
        when(siteRepo.findByNameContaining(query)).thenReturn(Optional.empty());

        final List<String> result = classToTest.search(query);

        assertThat(result.size(), equalTo(0));
    }

    @Test
    public void sites() {
        final List<OutcomeBean> outcomes = new ArrayList<>();

        when(outcomeRepo.findByAccountOrderByStartTimeDesc(any())).thenReturn(outcomes);
        when(principal.getPrincipal()).thenReturn(getPricipal());

        final String result = classToTest.sites(null, model, principal);

        verify(model).addAttribute("outcomes", outcomes);

        assertThat(result, equalTo("sites"));
    }

    @Test
    public void startError() {
        final StartModelBean startModel = new StartModelBean();
        startModel.setName(" ");
        when(principal.getPrincipal()).thenReturn(getPricipal());

        final String result = classToTest.start(startModel, model, principal);

        assertThat(result, equalTo("sites"));
        assertThat(startModel.isError(), equalTo(true));
    }

    @Test
    public void start() {
        final StartModelBean startModel = new StartModelBean();
        startModel.setName(SITE_NAME);

        final SiteBean site = new SiteBean();
        site.setName(SITE_NAME);
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setId(1);
        final SolarSystemBean system = new SolarSystemBean();
        system.setId(123123);

        when(siteRepo.findByName(SITE_NAME)).thenReturn(Optional.of(site));
        when(outcomeRepo.save(any())).thenReturn(outcome);
        when(principal.getPrincipal()).thenReturn(getPricipal());
        when(locationService.getLocation(account)).thenReturn(system);

        when(locationService.getShip(account)).thenReturn(new ShipBean());

        final String result = classToTest.start(startModel, model, principal);

        assertThat(result, equalTo("redirect:/site/1/edit"));
        assertThat(startModel.isError(), equalTo(false));
    }

    @Test
    public void startLocationError() {
        final StartModelBean startModel = new StartModelBean();
        startModel.setName(SITE_NAME);
        final SiteBean site = new SiteBean();
        site.setName(SITE_NAME);

        when(siteRepo.findByName(SITE_NAME)).thenReturn(Optional.of(site));
        when(locationService.getLocation(account)).thenReturn(null);
        when(principal.getPrincipal()).thenReturn(getPricipal());

        final String result = classToTest.start(startModel, model, principal);

        assertThat(result, equalTo("sites"));
        assertThat(startModel.isError(), equalTo(true));
    }

    @Test
    public void startSiteNotFound() {
        final StartModelBean startModel = new StartModelBean();
        startModel.setName(SITE_NAME);

        final SolarSystemBean system = new SolarSystemBean();
        system.setId(123123);

        final ArgumentCaptor<OutcomeBean> argument = ArgumentCaptor.forClass(OutcomeBean.class);

        when(siteRepo.findByName(SITE_NAME)).thenReturn(Optional.empty());
        when(outcomeRepo.save(argument.capture())).then(returnsFirstArg());
        when(principal.getPrincipal()).thenReturn(getPricipal());
        when(locationService.getLocation(account)).thenReturn(system);

        when(locationService.getShip(account)).thenReturn(new ShipBean());

        final String result = classToTest.start(startModel, model, principal);

        assertThat(result, equalTo("redirect:/site/0/edit"));
        assertThat(startModel.isError(), equalTo(false));
        assertThat(argument.getValue().getSiteName(), equalTo(SITE_NAME));
    }

    @Test
    public void show() {
        final long id = 5;
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setAccount(account);
        when(outcomeRepo.findById(id)).thenReturn(Optional.of(outcome));
        when(principal.getPrincipal()).thenReturn(getPricipal());

        final String result = classToTest.show(model, principal, id);

        verify(model).addAttribute("outcome", outcome);
        assertThat(result, equalTo("site"));
    }

    @Test
    public void showNotAllowed() {
        final long id = 5;
        final OutcomeBean outcome = new OutcomeBean();
        final AccountBean account1 = new AccountBean();
        account1.setCharacterId(CHARACTER_ID);
        final AccountBean account2 = new AccountBean();
        account2.setCharacterId(CHARACTER_ID + 1);
        outcome.setAccount(account1);
        when(outcomeRepo.findById(id)).thenReturn(Optional.of(outcome));
        when(principal.getPrincipal()).thenReturn(new EveOAuth2User(account2));

        assertThrows(AccessDeniedException.class, () -> classToTest.show(model, principal, id));
    }

    @Test
    public void showNotFound() {
        final long id = 5;
        when(outcomeRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> classToTest.show(model, principal, id));
    }

    @Test
    public void save() {
        final LocalDateTime now = LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusSeconds(1);
        final LootBean lootBean = new LootBean();
        lootBean.setValue(BOUNTY + REWARD);
        final OutcomeModelBean outcome = new OutcomeModelBean();
        outcome.setLootContent(LOOT);
        outcome.setFaction(true);
        outcome.setEscalation(true);
        outcome.setBountyValue(BOUNTY);
        outcome.setRewardValue(REWARD);
        final OutcomeBean outcomeDb = new OutcomeBean();
        outcomeDb.setLoot(new ArrayList<>());
        outcomeDb.setAccount(account);
        when(outcomeRepo.findById(OUTCOME_ID)).thenReturn(Optional.of(outcomeDb));
        when(principal.getPrincipal()).thenReturn(getPricipal());
        when(contentParserService.parse(LOOT)).thenReturn(Arrays.asList(lootBean));

        final String result = classToTest.save(model, outcome, principal, OUTCOME_ID);

        final ArgumentCaptor<OutcomeBean> ceptor = ArgumentCaptor.forClass(OutcomeBean.class);
        verify(outcomeRepo).save(ceptor.capture());

        verify(model).addAttribute("outcome", outcomeDb);

        assertThat(result, equalTo("site"));
        final OutcomeBean value = ceptor.getValue();
        assertThat(value.getLootValue(), equalTo((long) lootBean.getValue()));
        assertThat(value.getEndTime().isAfter(now), equalTo(true));
    }

    @Test
    public void saveDateOverwrite() {
        final LocalDateTime now = LocalDateTime.now(PvEApplication.DEFAULT_ZONE);
        final LocalDateTime old = now.minusHours(1);
        final LootBean lootBean = new LootBean();
        lootBean.setValue(BOUNTY + REWARD);
        final OutcomeModelBean outcome = new OutcomeModelBean();
        outcome.setLootContent(LOOT);
        outcome.setFaction(true);
        outcome.setEscalation(true);
        outcome.setBountyValue(BOUNTY);
        outcome.setRewardValue(REWARD);
        outcome.setEndTime(now);
        final OutcomeBean outcomeDb = new OutcomeBean();
        outcomeDb.setLoot(new ArrayList<>());
        outcomeDb.setEndTime(old);
        outcomeDb.setAccount(account);
        when(outcomeRepo.findById(OUTCOME_ID)).thenReturn(Optional.of(outcomeDb));
        when(principal.getPrincipal()).thenReturn(getPricipal());
        when(contentParserService.parse(LOOT)).thenReturn(Arrays.asList(lootBean));

        classToTest.save(model, outcome, principal, OUTCOME_ID);

        final ArgumentCaptor<OutcomeBean> ceptor = ArgumentCaptor.forClass(OutcomeBean.class);
        verify(outcomeRepo).save(ceptor.capture());
        assertThat(ceptor.getValue().getEndTime(), equalTo(now));
    }

    @Test
    public void saveSetDate() {
        final LocalDateTime old = LocalDateTime.now(PvEApplication.DEFAULT_ZONE).minusHours(1);
        final LootBean lootBean = new LootBean();
        lootBean.setValue(BOUNTY + REWARD);
        final OutcomeModelBean outcome = new OutcomeModelBean();
        outcome.setLootContent(LOOT);
        outcome.setFaction(true);
        outcome.setEscalation(true);
        outcome.setBountyValue(BOUNTY);
        outcome.setRewardValue(REWARD);
        outcome.setEndTime(old);
        final OutcomeBean outcomeDb = new OutcomeBean();
        outcomeDb.setLoot(new ArrayList<>());
        final AccountBean account = new AccountBean();
        account.setCharacterId(CHARACTER_ID);
        outcomeDb.setAccount(account);
        when(outcomeRepo.findById(OUTCOME_ID)).thenReturn(Optional.of(outcomeDb));
        when(principal.getPrincipal()).thenReturn(getPricipal());
        when(contentParserService.parse(LOOT)).thenReturn(Arrays.asList(lootBean));

        classToTest.save(model, outcome, principal, OUTCOME_ID);

        final ArgumentCaptor<OutcomeBean> ceptor = ArgumentCaptor.forClass(OutcomeBean.class);
        verify(outcomeRepo).save(ceptor.capture());
        assertThat(ceptor.getValue().getEndTime(), equalTo(old));
    }

    private EveOAuth2User getPricipal() {
        return new EveOAuth2User(account);
    }

    @Test
    public void getLootComparator() {
        final List<LootBean> list = new ArrayList<>();
        list.add(new LootBean(1, null, 1, 3d));
        list.add(new LootBean(2, null, 1, 0d));
        list.add(new LootBean(3, null, 1, 1d));

        Collections.sort(list, SitesController.getLootComparator());

        assertThat(list.get(0).getTypeId(), equalTo(2));
        assertThat(list.get(1).getTypeId(), equalTo(3));
    }

    public void delete() {
        final long id = 5;
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setAccount(account);
        when(outcomeRepo.findById(id)).thenReturn(Optional.of(outcome));
        when(principal.getPrincipal()).thenReturn(getPricipal());

        classToTest.delete(model, principal, id);

        verify(outcomeRepo).delete(outcome);
    }

}
