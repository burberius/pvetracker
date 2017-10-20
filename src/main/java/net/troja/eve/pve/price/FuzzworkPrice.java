package net.troja.eve.pve.price;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * { "34": { "buy": { "weightedAverage": "4.02183950149", "max": "5.1", "min":
 * "0.01", "stddev": "1.16138404203", "median": "4.155", "volume":
 * "15673925509.0", "orderCount": "54", "percentile": "4.75609144952" }, "sell":
 * { "weightedAverage": "5.61188059783", "max": "235243.0", "min": "4.79",
 * "stddev": "22433.51989", "median": "5.78", "volume": "32330442244.0",
 * "orderCount": "110", "percentile": "4.80420075444" } }
 */
@Data
@NoArgsConstructor
public class FuzzworkPrice {
    private FuzzworkPriceValues buy;
    private FuzzworkPriceValues sell;
}
