package com.bank.service;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.NotAllowedException;
import com.bank.exception.StatusNotAllowedException;
import com.bank.repository.TransactionRepository;
import com.bank.dto.request.IbanTransactionRequestDto;
import com.bank.dto.request.UsernameTransactionRequestDto;
import com.bank.dto.response.IbanTransactionResponseDto;
import com.bank.dto.response.UsernameTransactionResponseDto;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.util.TransactionStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public TransactionService(UserService userService, TransactionRepository transactionRepository,
                                                                                            ModelMapper modelMapper) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    public List<UsernameTransactionResponseDto> getAllByUser(Long userId) {
        List<Transaction> senderTransactions = transactionRepository.getTransactionsBySenderId(userId);
        List<Transaction> receiverTransactions = transactionRepository.getTransactionsByReceiverId(userId);

        String userName = userService.getById(userId).getFullName();

        List<UsernameTransactionResponseDto> senderTransactionDtos =
                mapTransactionsToDtos(senderTransactions, userName, true);

        List<UsernameTransactionResponseDto> receiverTransactionDtos =
                mapTransactionsToDtos(receiverTransactions, userName, false);

        List<UsernameTransactionResponseDto> userTransactionDtos = new ArrayList<>();
        userTransactionDtos.addAll(senderTransactionDtos);
        userTransactionDtos.addAll(receiverTransactionDtos);

        userTransactionDtos.sort(Comparator.comparing(UsernameTransactionResponseDto::getDate).reversed());

        return userTransactionDtos;
    }

    public UsernameTransactionResponseDto createByUsername(User sender, UsernameTransactionRequestDto requestDto) {
        if (requestDto.getStatus() == TransactionStatus.REJECTED) {
            throw new StatusNotAllowedException();
        }

        User receiver = userService.getByUsername(requestDto.getUsername());

        if (requestDto.getAmount() < 5 || sender.getId().equals(receiver.getId())) {
            throw new NotAllowedException();
        }

        Date date = new Date(System.currentTimeMillis());
        String type = requestDto.getStatus() == TransactionStatus.ACCEPTED ? "sent" : "requested";
        Transaction transaction = new Transaction(sender, receiver, requestDto.getAmount(), requestDto.getDescription(),
                                                                                   type, date, requestDto.getStatus());

        validateTransaction(transaction, requestDto.getAmount(), requestDto.getStatus());

        UsernameTransactionResponseDto responseDto =
                modelMapper.map(transactionRepository.save(transaction), UsernameTransactionResponseDto.class);

        responseDto.setSenderName(sender.getFullName());
        responseDto.setReceiverName(userService.getByUsername(requestDto.getUsername()).getFullName());

        return responseDto;
    }

    public IbanTransactionResponseDto createByIban(User sender, IbanTransactionRequestDto requestDto) {
        if (requestDto.getStatus() == TransactionStatus.REJECTED) {
            throw new StatusNotAllowedException();
        }

        User receiver = userService.getByIban(requestDto.getIban());

        if (requestDto.getAmount() < 5 || sender.getId().equals(receiver.getId())) {
            throw new NotAllowedException();
        }

        Date date = new Date(System.currentTimeMillis());
        String type = requestDto.getStatus() == TransactionStatus.ACCEPTED ? "sent" : "requested";
        Transaction transaction = new Transaction(sender, receiver, requestDto.getAmount(), requestDto.getDescription(),
                                                                                    type, date, requestDto.getStatus());

        validateTransaction(transaction, requestDto.getAmount(), requestDto.getStatus());

        IbanTransactionResponseDto responseDto =
                modelMapper.map(transactionRepository.save(transaction), IbanTransactionResponseDto.class);

        responseDto.setName(sender.getFullName());

        return responseDto;
    }

    public void updateTransaction(Long id, Long userId, TransactionStatus status) {
        Transaction transaction = transactionRepository.getOne(id);

        if (transaction.getStatus() != TransactionStatus.PENDING ||
                !userId.equals(transaction.getReceiver().getId())) {
            throw new NotAllowedException();
        }

        if (status == TransactionStatus.PENDING) {
            throw new StatusNotAllowedException();
        }

        if (transaction.getReceiver().getDeposit() - transaction.getAmount() < 0) {
            throw new InsufficientFundsException();
        }

        transaction.setStatus(status);

        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            transaction.getReceiver().setDeposit(transaction.getReceiver().getDeposit() - transaction.getAmount());
            transaction.getSender().setDeposit(transaction.getSender().getDeposit() + transaction.getAmount());
        }

        transactionRepository.save(transaction);
    }

    private List<UsernameTransactionResponseDto> mapTransactionsToDtos(List<Transaction> transactionList,
                                                                                    String userName, boolean isSender) {
        return transactionList.stream().map(transaction -> {
            UsernameTransactionResponseDto responseDto =
                    modelMapper.map(transaction, UsernameTransactionResponseDto.class);

            String senderName = isSender ? userName : transaction.getSender().getFullName();
            String receiverName = isSender ? transaction.getReceiver().getFullName() : userName;

            responseDto.setSenderName(senderName);
            responseDto.setReceiverName(receiverName);

            return responseDto;
        }).collect(Collectors.toList());
    }

    private void validateTransaction(Transaction transaction, Double amount, TransactionStatus status) {
        if (status == TransactionStatus.ACCEPTED) {
            User sender = transaction.getSender();
            User receiver = transaction.getReceiver();

            if (sender.getDeposit() - amount < 0) {
                throw new InsufficientFundsException();
            }

            sender.setDeposit(sender.getDeposit() - amount);
            userService.setUser(sender);

            receiver.setDeposit(receiver.getDeposit() + amount);
        }
    }

    public List<UsernameTransactionResponseDto> getSpecificNumber(Long id, int nr) {
        if (nr <= 0) {
            throw new NotAllowedException();
        }

        List<UsernameTransactionResponseDto> responseDtos = getAllByUser(id);

        return getAllByUser(id).subList(0, Math.min(responseDtos.size(), nr));
    }
}
