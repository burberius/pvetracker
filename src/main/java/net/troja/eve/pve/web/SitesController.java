package net.troja.eve.pve.web;

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
