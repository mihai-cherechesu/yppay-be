package com.bank.controller;

import com.bank.dto.request.IbanTransactionRequestDto;
import com.bank.dto.request.UsernameTransactionRequestDto;
import com.bank.dto.response.IbanTransactionResponseDto;
import com.bank.dto.response.UsernameTransactionResponseDto;
import com.bank.security.SecurityUser;
import com.bank.service.TransactionService;
import com.bank.util.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(path = "get/all")
    public ResponseEntity<?> getAllByUser(@AuthenticationPrincipal SecurityUser securityUser) {
        List<UsernameTransactionResponseDto> transactionDtosList =
                transactionService.getAllByUser(securityUser.getUser().getId());

        return ResponseEntity.status(HttpStatus.OK).body(transactionDtosList);
    }

    @GetMapping(path = "get/{nr}")
    public ResponseEntity<?> getSpecificNumber(@AuthenticationPrincipal SecurityUser securityUser,
                                               @PathVariable int nr) {
        List<UsernameTransactionResponseDto> transactionDtosList =
                transactionService.getSpecificNumber(securityUser.getUser().getId(), nr);

        return ResponseEntity.status(HttpStatus.OK).body(transactionDtosList);
    }

    @PostMapping(path = "create/username")
    public ResponseEntity<?> sendByUsername(@AuthenticationPrincipal SecurityUser securityUser,
                                            @RequestBody UsernameTransactionRequestDto usernameTransactionRequestDto) {

        UsernameTransactionResponseDto responseDto =
                            transactionService.createByUsername(securityUser.getUser(), usernameTransactionRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(path = "create/iban")
    public ResponseEntity<?> sendByIban(@AuthenticationPrincipal SecurityUser securityUser,
                                        @RequestBody IbanTransactionRequestDto ibanTransactionRequestDto) {

        IbanTransactionResponseDto responseDto =
                            transactionService.createByIban(securityUser.getUser(), ibanTransactionRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(path = "update/{id}")
    public ResponseEntity<?> updateTransaction(@AuthenticationPrincipal SecurityUser securityUser,
                                               @PathVariable Long id,
                                               @RequestParam String status) {

        transactionService.updateTransaction(id, securityUser.getUser().getId(), TransactionStatus.valueOf(status));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
