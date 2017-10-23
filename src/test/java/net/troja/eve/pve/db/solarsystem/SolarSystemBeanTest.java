package net.troja.eve.pve.db.solarsystem;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

public class SolarSystemBeanTest {
    @Test
    public void low() {
        final SolarSystemBean solarSystemBean = new SolarSystemBean();
        solarSystemBean.setSecurity(0.3253d);
        assertThat(solarSystemBean.getSecClass(), equalTo("sec-low"));
    }

    @Test
    public void sec07() {
        final SolarSystemBean solarSystemBean = new SolarSystemBean();
        solarSystemBean.setSecurity(0.6573d);
        assertThat(solarSystemBean.getSecClass(), equalTo("sec-07"));
    }
}
