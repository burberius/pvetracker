package net.troja.eve.pve.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartModel {
    private String name;
    private boolean error;
}
