package net.troja.eve.pve.db.outcome;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

public class LootBeanTest {
    @Test
    public void getValueString() {
        final LootBean loot = new LootBean();
        loot.setValue(1234567);

        assertThat(loot.getValueString(), equalTo("1.234.567 ISK"));
    }

    @Test
    public void getValueStringMulti() {
        final LootBean loot = new LootBean();
        loot.setValue(1234);
        loot.setCount(10);

        assertThat(loot.getValueString(), equalTo("12.340 ISK (1.234 ISK per unit)"));
    }
}
