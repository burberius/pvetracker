package net.troja.eve.pve.db.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MonthOverviewStatBean {
    private LocalDate date;
    private long value;

    public MonthOverviewStatBean(final LocalDate date, final long lootValue, final long bountyValue, final long rewardValue) {
        super();
        this.date = date;
        this.value = lootValue + bountyValue + rewardValue;
    }
}
