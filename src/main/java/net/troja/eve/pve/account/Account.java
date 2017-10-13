package net.troja.eve.pve.account;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Account {
    @Id
    private long characterId;
    private String characterName;
    private String characterOwnerHash;
    private Date created = new Date();
    private Date lastLogin = new Date();
    private String refreshToken;
}
