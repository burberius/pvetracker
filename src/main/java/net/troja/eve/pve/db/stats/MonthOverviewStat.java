package net.troja.eve.pve.db.stats;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MonthOverviewStat {
    private LocalDate date;
    private double value;

    public MonthOverviewStat(final LocalDate date, final double value) {
        super();
        this.date = date;
        this.value = value;
    }
}
