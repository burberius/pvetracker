package net.troja.eve.pve.db.contract;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
public class ContractBean {
    @Id
    private int contractId;
    private int typeId;
    private double price;
    private OffsetDateTime dateExpired;
}
