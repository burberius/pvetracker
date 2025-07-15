package net.troja.eve.pve.db.contract;

import lombok.Data;

import static java.lang.Math.round;

@Data
public class ContractPrice {
    private final int typeId;
    private final double price;

    public long getPriceLong() {
        return round(price);
    }
}
