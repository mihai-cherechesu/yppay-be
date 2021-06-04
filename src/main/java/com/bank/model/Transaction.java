package com.bank.model;

import com.bank.util.TransactionStatus;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "transaction")
@NoArgsConstructor
@DynamicUpdate
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull
    private User sender;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull
    private User receiver;

    @NotNull
    private Double amount;

    private String description;

    private String type;

    @NotNull
    private TransactionStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Transaction(User sender, User receiver, Double amount, String description, String type,
                                                                                Date date, TransactionStatus status) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.date = date;
        this.status = status;
    }

}
