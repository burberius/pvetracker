package net.troja.eve.pve.price.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContractPriceBean {
    @JsonProperty("type_id")
    private int typeId;
    @JsonProperty("type_name")
    private String typeName;
    private double median;
    private double average;
    private double minimum;
    private double maximum;
    @JsonProperty("five_percent")
    private double fivePercent;
    private int contracts;
}
