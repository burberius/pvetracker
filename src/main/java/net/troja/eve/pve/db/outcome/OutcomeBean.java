package net.troja.eve.pve.db.outcome;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.sites.SiteBean;
import net.troja.eve.pve.db.solarsystem.SolarSystemBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "outcome")
public class OutcomeBean {
    private static final int SECONDS2MINUTES = 60;
    private static final int SECONDS2HOURS = 3600;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountBean account;
    @ManyToOne
    @JoinColumn(name = "system_id")
    private SolarSystemBean system;
    @ManyToOne
    @JoinColumn(name = "ship_id")
    private ShipBean ship;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private SiteBean site;
    private String siteName;
    private LocalDateTime startTime = LocalDateTime.now(PvEApplication.DEFAULT_ZONE);
    private LocalDateTime endTime;
    private boolean faction;
    private boolean escalation;
    private long bountyValue;
    private long rewardValue;
    private long lootValue;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "outcome_id")
    private List<LootBean> loot;

    public OutcomeBean() {
        super();
    }

    public OutcomeBean(final AccountBean account, final SolarSystemBean system, final ShipBean ship,
                       final String siteName, final SiteBean site) {
        super();
        this.account = account;
        this.system = system;
        this.ship = ship;
        this.siteName = siteName;
        this.site = site;
    }

    public void addLoot(final LootBean lootEntry) {
        if (loot == null) {
            loot = new ArrayList<>();
        }
        loot.add(lootEntry);
    }

    public String getDuration() {
        if (endTime == null) {
            return "running";
        }
        final Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();
        final StringBuilder result = new StringBuilder();
        final int hours = (int) Math.floorDiv(seconds, SECONDS2HOURS);
        if (hours > 0) {
            result.append(hours).append("h ");
        }
        seconds = Math.floorMod(seconds, SECONDS2HOURS);
        final int minutes = (int) Math.floorDiv(seconds, SECONDS2MINUTES);
        if (minutes > 0) {
            result.append(minutes).append("m ");
        }
        result.append(Math.floorMod(seconds, SECONDS2MINUTES)).append('s');
        return result.toString();
    }

    public long getSumValue() {
        return lootValue + bountyValue + rewardValue;
    }
}
