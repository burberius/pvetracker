package net.troja.eve.pve.db.stats;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MonthOverviewStatBean {
    private LocalDate date;
    private double value;

    public MonthOverviewStatBean(final LocalDate date, final double value) {
        super();
        this.date = date;
        this.value = value;
    }
}
