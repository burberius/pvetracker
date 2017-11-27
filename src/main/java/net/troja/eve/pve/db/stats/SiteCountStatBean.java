package net.troja.eve.pve.db.stats;

import lombok.Data;

@Data
public class SiteCountStatBean {
    private String name;
    private long count;

    public SiteCountStatBean(final String name, final long count) {
        super();
        this.name = name;
        this.count = count;
    }
}
