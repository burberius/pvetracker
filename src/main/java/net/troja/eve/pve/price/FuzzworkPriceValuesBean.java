package net.troja.eve.pve.price;

import lombok.Data;

@Data
public class FuzzworkPriceValuesBean {
    private double weightedAverage;
    private double max;
    private double min;
    private double stddev;
    private double median;
    private double volume;
    private double orderCount;
    private double percentile;

    public FuzzworkPriceValuesBean() {
        super();
    }
}
