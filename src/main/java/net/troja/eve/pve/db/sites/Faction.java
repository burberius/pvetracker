package net.troja.eve.pve.db.sites;

public enum Faction {
    AMARR_EMPIRE("Amarr Empire"),
    AMMATAR_MANDATE("Ammatar Mandate"),
    KHANID_KINGDOM("Khanid Kingdom"),
    BLOOD_RAIDERS("Blood Raiders"),
    SANSHAS_NATION("Sansha's Nation"),
    CALDARI_STATE("Caldari State"),
    GURISTAS_PIRATES("Guristas Pirates"),
    MORDUS_LEGION("Mordu's Legion"),
    GALLENTE_FEDERATION("Gallente Federation"),
    INTAKI_SYNDICATE("Intaki Syndicate"),
    O_R_E("O.R.E."),
    SERPENTIS("Serpentis"),
    MINMATAR_REPUBLIC("Minmatar Republic"),
    ANGEL_CARTEL("Angel Cartel"),
    THUKKER_TRIBE("Thukker Tribe"),
    CONCORD_ASSEMBLY("Concord Assembly"),
    INTERBUS("Interbus"),
    SISTERS_OF_EVE("Sisters of EVE");

    private String name;

    Faction(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
