package net.troja.eve.pve.price;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * "weightedAverage": "4.02183950149", "max": "5.1", "min": "0.01", "stddev":
 * "1.16138404203", "median": "4.155", "volume": "15673925509.0", "orderCount":
 * "54", "percentile": "4.75609144952"
 */
@Data
@NoArgsConstructor
public class FuzzworkPriceValues {
    private double weightedAverage;
    private double max;
    private double min;
    private double stddev;
    private double median;
    private double volume;
    private double orderCount;
    private double percentile;
}
