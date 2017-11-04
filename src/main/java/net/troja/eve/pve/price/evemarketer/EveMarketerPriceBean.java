package net.troja.eve.pve.price.evemarketer;

import lombok.Data;

@Data
public class EveMarketerPriceBean {
    private EveMarketerPriceValueBean buy;
    private EveMarketerPriceValueBean sell;

    public EveMarketerPriceBean() {
        super();
    }
}
