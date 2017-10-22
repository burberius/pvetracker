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
import java.util.List;
import java.util.Optional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.sites.SiteRepository;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;
import net.troja.eve.pve.esi.LocationService;

@Controller
@RequestMapping("/sites")
public class SitesController {
    private static final Logger LOGGER = LogManager.getLogger(SitesController.class);

    @Autowired
    private SiteRepository siteRepo;
    @Autowired
    private OutcomeRepository outcomeRepo;
    @Autowired
    private LocationService locationService;

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
            LOGGER.info("Got " + saved);
            return "redirect:/sites/" + saved.getId();
        }
    }

    @GetMapping("/{id}")
    public String site(final Model model, final Principal principal, @PathVariable final long id) {
        final Optional<OutcomeBean> outcomeResult = outcomeRepo.findById(id);
        if (!outcomeResult.isPresent()) {
            throw new NotFoundException();
        }
        final OutcomeBean outcome = outcomeResult.get();
        final AccountBean account = (AccountBean) ((OAuth2Authentication) principal).getPrincipal();
        if (outcome.getAccount().getCharacterId() != account.getCharacterId()) {
            throw new ForbiddenException();
        }
        model.addAttribute("outcome", outcome);
        return "site";
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
