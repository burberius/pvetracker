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

import lombok.RequiredArgsConstructor;
import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.content.ContentParserService;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.outcome.LootRepository;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.db.stats.SiteCountStatBean;
import net.troja.eve.pve.discord.DiscordService;
import net.troja.eve.pve.esi.LocationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/site")
public class SitesController {
    private static final String MODEL_OUTCOME = "outcome";

    private final SiteRepository siteRepo;
    private final OutcomeRepository outcomeRepo;
    private final LootRepository lootRepo;
    private final LocationService locationService;
    private final ContentParserService contentParserService;
    private final DiscordService discordService;

    @GetMapping
    public String sites(final StartModelBean startModel, final Model model, final Principal principal) {
        AccountBean account = ControllerHelper.getAccount(principal);
        final List<OutcomeBean> outcomes = outcomeRepo.findByAccountOrderByStartTimeDesc(account);
        model.addAttribute("outcomes", outcomes);
        final List<SiteCountStatBean> siteCountStats = outcomeRepo.getSiteCountStats(account, PageRequest.of(0, 16));
        siteCountStats.sort(Comparator.comparing(SiteCountStatBean::getName));
        model.addAttribute("stats", siteCountStats);
        return "sites";
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam("q") final String query) {
        List<String> result = new ArrayList<>();
        final Optional<List<SiteBean>> searchResult = siteRepo.findByNameContainingIgnoreCase(query);
        if (searchResult.isPresent()) {
            result = searchResult.get().stream().map(SiteBean::getName).collect(Collectors.toList());
        }
        return result;
    }

    @PostMapping
    public String start(final StartModelBean startModel, final Model model, final Principal principal) {
        AccountBean account = ControllerHelper.getAccount(principal);
        final String name = startModel.getName();
        if (StringUtils.isBlank(name)) {
            startModel.setError(true);
            startModel.setErrorMessage("Looks like you didn't enter a site");
            return sites(startModel, model, principal);
        } else {
            final Optional<SiteBean> site = siteRepo.findByName(name);
            String siteString;
            if (site.isPresent()) {
                siteString = site.get().getName();
            } else {
                siteString = name;
            }
            final SolarSystemBean system = locationService.getLocation(account);
            final ShipBean ship = locationService.getShip(account);
            if (system == null || ship == null) {
                startModel.setError(true);
                startModel.setErrorMessage("Location and ship could not be retrieved from the EVE API, please try again in a few seconds.");
                return sites(startModel, model, principal);
            }
            final OutcomeBean outcome = new OutcomeBean(account, system, ship, siteString, site.orElse(null));
            final OutcomeBean saved = outcomeRepo.save(outcome);
            return "redirect:/site/" + saved.getId() + "/edit";
        }
    }

    @GetMapping("/{id}")
    public String show(final Model model, final Principal principal, @PathVariable final long id) {
        model.addAttribute(MODEL_OUTCOME, getOutcome(principal, id));
        return "site";
    }

    @GetMapping("/{id}/edit")
    public String edit(final Model model, final Principal principal, @PathVariable final long id) {
        model.addAttribute(MODEL_OUTCOME, new OutcomeModelBean(getOutcome(principal, id)));
        return "siteedit";
    }

    @PostMapping("/{id}")
    public String save(final Model model, final OutcomeModelBean outcome, final Principal principal, @PathVariable final long id) {
        final OutcomeBean outcomeDb = getOutcome(principal, id);
        if (outcomeDb.getId() != outcome.getId()) {
            throw new AccessDeniedException("You are not allowed to view that");
        }
        outcomeDb.setStartTime(outcome.getStartTime());
        boolean isNew = false;
        if (outcomeDb.getEndTime() == null && outcome.getEndTime() == null) {
            outcomeDb.setEndTime(LocalDateTime.now(PvEApplication.DEFAULT_ZONE));
            isNew = true;
        } else {
            outcomeDb.setEndTime(outcome.getEndTime());
        }
        outcomeDb.setFaction(outcome.isFaction());
        outcomeDb.setEscalation(outcome.isEscalation());
        outcomeDb.setBountyValue(outcome.getBountyValue());
        outcomeDb.setRewardValue(outcome.getRewardValue());
        final List<LootBean> loot = contentParserService.parse(outcome.getLootContent());
        loot.sort(getLootComparator());
        if(!outcomeDb.getLoot().isEmpty()) {
            lootRepo.deleteAll(outcomeDb.getLoot());
            outcomeDb.getLoot().clear();
        }
        outcomeDb.getLoot().addAll(loot);
        outcomeDb.setLootValue(getLootValue(outcomeDb.getLoot()));
        outcomeRepo.save(outcomeDb);
        if(isNew) {
            discordService.postOutcome(outcomeDb);
        }
        model.addAttribute(MODEL_OUTCOME, outcomeDb);
        return "site";
    }

    @GetMapping("/{id}/delete")
    public String delete(final Model model, final Principal principal, @PathVariable final long id) {
        final OutcomeBean outcomeDb = getOutcome(principal, id);
        outcomeRepo.delete(outcomeDb);
        return "redirect:/site";
    }

    public static Comparator<LootBean> getLootComparator() {
        return Comparator.comparingDouble(LootBean::getValue);
    }

    private static long getLootValue(final List<LootBean> loot) {
        long isk = 0;
        for (final LootBean entry : loot) {
            isk += Math.round(entry.getValue() * entry.getCount());
        }
        return isk;
    }

    private OutcomeBean getOutcome(final Principal principal, final long id) {
        final Optional<OutcomeBean> outcomeResult = outcomeRepo.findById(id);
        if (outcomeResult.isEmpty()) {
            throw new NotFoundException();
        }
        final OutcomeBean outcome = outcomeResult.get();
        final AccountBean account = ControllerHelper.getAccount(principal);
        if (outcome.getAccount().getCharacterId() != account.getCharacterId()) {
            throw new AccessDeniedException("You are not allowed to view that");
        }
        return outcome;
    }
}
