package net.troja.eve.pve.price.evemarketer;

import lombok.Data;

@Data
public class EveMarketerPriceForQueryBean {
    private boolean bid;
    private int[] types;
    private int[] regions;
    private int[] systems;
    private int hours;
    private int minq;
}
