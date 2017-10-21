package net.troja.eve.pve.price;

import lombok.Data;

@Data
public class FuzzworkPriceBean {
    private FuzzworkPriceValuesBean buy;
    private FuzzworkPriceValuesBean sell;

    public FuzzworkPriceBean() {
        super();
    }
}
