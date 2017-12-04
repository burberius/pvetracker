package net.troja.eve.pve.db.stats;

import lombok.Data;

@Data
public class ValuesStatBean {
    private long reward;
    private long bounty;
    private long loot;

    public ValuesStatBean(final long reward, final long bounty, final long loot) {
        super();
        this.reward = reward;
        this.bounty = bounty;
        this.loot = loot;
    }
}
