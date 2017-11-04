package net.troja.eve.pve.price.evemarketer;

import lombok.Data;

@Data
public class EveMarketerPriceValueBean {
    private EveMarketerPriceForQueryBean forQuery;
    private double volume;
    private double wavg;
    private double avg;
    private double variance;
    private double stdDev;
    private double median;
    private double fivePercent;
    private double max;
    private double min;
    private boolean highToLow;
    private long generated;

    public EveMarketerPriceValueBean() {
        super();
    }
}
