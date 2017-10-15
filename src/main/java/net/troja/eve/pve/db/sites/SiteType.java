package net.troja.eve.pve.db.sites;

public enum SiteType {
    ANOMALY("Anomaly"),
    SIGNATURE("Signature"),
    DATA("Data"),
    RELIC("Relic"),
    GAS("Gas"),
    ESCALATION("Escalation");

    private String name;

    SiteType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
