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
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.troja.eve.pve.db.account.Account;
import net.troja.eve.pve.db.outcome.Outcome;
import net.troja.eve.pve.db.outcome.OutcomeRepository;
import net.troja.eve.pve.db.sites.Site;
import net.troja.eve.pve.db.sites.SiteRepository;

@Controller
@RequestMapping("/sites")
public class SitesController {
    private static final Logger LOGGER = LogManager.getLogger(SitesController.class);

    @Autowired
    private SiteRepository siteRepo;
    @Autowired
    private OutcomeRepository outcomeRepo;

    public SitesController() {
        super();
    }

    @GetMapping
    public String sites(final StartModel model) {
        return "sites";
    }

    @PostMapping
    public String start(final StartModel model, final Principal principal) {
        final Account account = (Account) ((OAuth2Authentication) principal).getPrincipal();
        LOGGER.info(account);
        final String name = model.getName();
        if (StringUtils.isBlank(name)) {
            model.setError(true);
            return "sites";
        } else {
            final Optional<Site> site = siteRepo.findByName(name);
            String siteString;
            if (site.isPresent()) {
                siteString = site.get().toString();
            } else {
                siteString = name;
            }
            final String system = "nix";
            final String ship = "da";
            final Outcome outcome = new Outcome(account, system, ship, siteString, site.orElse(null));
            final Outcome saved = outcomeRepo.save(outcome);
            LOGGER.info("Got " + saved);
            return "redirect:/sites/" + saved.getId();
        }
    }
}
