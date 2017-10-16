package net.troja.eve.pve.db.outcome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.troja.eve.pve.db.account.Account;
import net.troja.eve.pve.db.sites.Site;

@Data
@NoArgsConstructor
@Entity
public class Outcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private String system;
    private String ship;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
    private String siteName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date start = new Date();
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    private boolean faction;
    private boolean escalation;
    private double bountyValue;
    private double lootValue;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "outcome_id")
    private List<Loot> loot = new ArrayList<>();

    public Outcome(final Account account, final String system, final String ship, final String siteName, final Site site) {
        super();
        this.account = account;
        this.system = system;
        this.ship = ship;
        this.siteName = siteName;
        this.site = site;
    }

    public void addLoot(final Loot lootEntry) {
        loot.add(lootEntry);
    }
}
