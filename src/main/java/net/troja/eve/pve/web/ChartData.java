package net.troja.eve.pve.web;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ChartData {
    private List<String> labels = new ArrayList<>();
    private List<Object> data = new ArrayList<>();

    public void addLabel(final String label) {
        labels.add(label);
    }

    public void addData(final Object dat) {
        data.add(dat);
    }
}
