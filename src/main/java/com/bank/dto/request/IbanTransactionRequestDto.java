package com.bank.dto.request;

import com.bank.util.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IbanTransactionRequestDto {

    private String iban;

    private Double amount;

    private String description;

    private TransactionStatus status;

}
