package net.troja.eve.pve.db.stats;

import lombok.Data;

@Data
public class SiteCountStat {
    private String name;
    private long count;

    public SiteCountStat(final String name, final long count) {
        super();
        this.name = name;
        this.count = count;
    }
}
