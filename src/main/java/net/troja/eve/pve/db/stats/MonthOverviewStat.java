package net.troja.eve.pve.db.stats;

import java.util.Date;

import lombok.Data;

@Data
public class MonthOverviewStat {
    private Date date;
    private double value;

    public MonthOverviewStat(final Date date, final double value) {
        super();
        this.date = date;
        this.value = value;
    }
}
