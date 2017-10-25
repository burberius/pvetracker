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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.troja.eve.pve.content.ContentParserService;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.esi.LocationService;

@Controller
@RequestMapping("/site")
public class SitesController {
    private static final String MODEL_OUTCOME = "outcome";

    private static final Logger LOGGER = LogManager.getLogger(SitesController.class);

    @Autowired
    private SiteRepository siteRepo;
    @Autowired
    private OutcomeRepository outcomeRepo;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ContentParserService contentParserService;

    public SitesController() {
        super();
    }

    @GetMapping
    public String sites(final StartModelBean startModel, final Model model, final Principal principal) {
        final AccountBean account = (AccountBean) ((OAuth2Authentication) principal).getPrincipal();
        final List<OutcomeBean> outcomes = outcomeRepo.findByAccountOrderByStartDesc(account);
        model.addAttribute("outcomes", outcomes);
        return "sites";
    }

    @PostMapping
    public String start(final StartModelBean model, final Principal principal) {
        final AccountBean account = (AccountBean) ((OAuth2Authentication) principal).getPrincipal();
        final String name = model.getName();
        if (StringUtils.isBlank(name)) {
            model.setError(true);
            return "sites";
        } else {
            final Optional<SiteBean> site = siteRepo.findByName(name);
            String siteString;
            if (site.isPresent()) {
                siteString = site.get().toString();
            } else {
                siteString = name;
            }
            final SolarSystemBean system = locationService.getLocation(account);
            final ShipBean ship = locationService.getShip(account);
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
        if (outcome.getEnd() == null) {
            outcomeDb.setEnd(LocalDateTime.now());
        }
        outcomeDb.setFaction(outcome.isFaction());
        outcomeDb.setEscalation(outcome.isEscalation());
        final List<LootBean> loot = contentParserService.parse(outcome.getLootContent());
        Collections.sort(loot, getLootComparator());
        outcomeDb.getLoot().addAll(loot);
        outcomeDb.setLootValue(getLootValue(outcomeDb.getLoot()));
        outcomeRepo.save(outcomeDb);
        model.addAttribute(MODEL_OUTCOME, outcomeDb);
        return "site";
    }

    public static Comparator<LootBean> getLootComparator() {
        return (final LootBean o1, final LootBean o2) -> {
            if (o1.getValue() == 0 && o1.getValue() < o2.getValue()) {
                return -1;
            } else if (o2.getValue() == 0 && o2.getValue() < o1.getValue()) {
                return 1;
            } else {
                return Double.compare(o1.getValue(), o2.getValue());
            }
        };
    }

    private static double getLootValue(final List<LootBean> loot) {
        double isk = 0;
        for (final LootBean entry : loot) {
            isk += entry.getValue() * entry.getCount();
        }
        return isk;
    }

    private OutcomeBean getOutcome(final Principal principal, final long id) {
        final Optional<OutcomeBean> outcomeResult = outcomeRepo.findById(id);
        if (!outcomeResult.isPresent()) {
            throw new NotFoundException();
        }
        final OutcomeBean outcome = outcomeResult.get();
        final AccountBean account = (AccountBean) ((OAuth2Authentication) principal).getPrincipal();
        if (outcome.getAccount().getCharacterId() != account.getCharacterId()) {
            throw new AccessDeniedException("You are not allowed to view that");
        }
        return outcome;
    }

    void setSiteRepo(final SiteRepository siteRepo) {
        this.siteRepo = siteRepo;
    }

    void setOutcomeRepo(final OutcomeRepository outcomeRepo) {
        this.outcomeRepo = outcomeRepo;
    }

    void setLocationService(final LocationService locationService) {
        this.locationService = locationService;
    }
}
