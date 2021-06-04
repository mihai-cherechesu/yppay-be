package com.bank.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity(name = "user")
@NoArgsConstructor
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

    private String fullName;

    private String iban;

    private double deposit;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Set<Transaction> sentTransactions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Set<Transaction> receivedTransactions = new LinkedHashSet<>();

    public User(String username, String password, String fullName, String iban, double deposit) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.iban = iban;
        this.deposit = deposit;
    }

}
