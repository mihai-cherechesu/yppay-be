package com.bank.dto.request;

import com.bank.util.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernameTransactionRequestDto {

    private String username;

    private Double amount;

    private String description;

    private TransactionStatus status;

}
