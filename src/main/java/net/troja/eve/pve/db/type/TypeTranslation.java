package net.troja.eve.pve.db.type;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class TypeTranslation {
    @Id
    private int id;
    private int typeId;
    private String language;
    private String name;
}
