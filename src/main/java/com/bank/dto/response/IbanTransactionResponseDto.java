package com.bank.dto.response;

import com.bank.util.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IbanTransactionResponseDto {

    private Long id;

    private String name;

    private String iban;

    private Double amount;

    private String description;

    private String type;

    private TransactionStatus status;

    private Date date;

}
