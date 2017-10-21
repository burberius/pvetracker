package net.troja.eve.pve.db.price;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name = "price")
public class PriceBean {
    @Id
    private int typeId;
    private double value;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    public PriceBean() {
        super();
    }

    public PriceBean(final int typeId, final double value, final Date created) {
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
