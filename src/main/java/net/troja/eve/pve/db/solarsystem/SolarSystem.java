package net.troja.eve.pve.db.solarsystem;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class SolarSystem {
    @Id
    private int id;
    private String name;
}
