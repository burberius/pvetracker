package net.troja.eve.pve.db.outcome;

import java.time.LocalDateTime;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

public class OutcomeBeanTest {
    @Test
    public void getDuration1() {
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setStart(LocalDateTime.of(2017, 10, 10, 10, 10, 10));
        outcome.setEnd(LocalDateTime.of(2017, 10, 10, 11, 11, 11));

        assertThat(outcome.getDuration(), equalTo("1h 1m 1s"));
    }

    @Test
    public void getDuration2() {
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setStart(LocalDateTime.of(2017, 10, 10, 10, 10, 10));
        outcome.setEnd(LocalDateTime.of(2017, 10, 10, 10, 23, 55));

        assertThat(outcome.getDuration(), equalTo("13m 45s"));
    }

    @Test
    public void getDurationNoEndYet() {
        final OutcomeBean outcome = new OutcomeBean();
        outcome.setStart(LocalDateTime.of(2017, 10, 10, 10, 10, 10));

        assertThat(outcome.getDuration(), equalTo("running"));
    }
}