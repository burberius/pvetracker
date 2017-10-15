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
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;
    private boolean faction;
    private boolean escalation;
    private double bountyValue;
    private double lootValue;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "outcome_id")
    private List<Loot> loot = new ArrayList<>();

    public void addLoot(final Loot lootEntry) {
        loot.add(lootEntry);
    }
}
