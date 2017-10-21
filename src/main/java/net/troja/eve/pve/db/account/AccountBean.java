package net.troja.eve.pve.db.account;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class AccountBean {
    @Id
    private int characterId;
    private String characterName;
    private String characterOwnerHash;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime lastLogin = LocalDateTime.now();
    private String refreshToken;

    public AccountBean() {
        super();
    }
}
