package net.troja.eve.pve.db.price;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "price")
public class PriceBean {
    @Id
    private int typeId;
    private double value;
    private LocalDateTime created = LocalDateTime.now();

    public PriceBean() {
        super();
    }

    public PriceBean(final int typeId, final double value, final LocalDateTime created) {
        super();
        this.typeId = typeId;
        this.value = value;
        this.created = created;
    }

    public PriceBean(final int typeId, final double value) {
        super();
        this.typeId = typeId;
        this.value = value;
    }
}
