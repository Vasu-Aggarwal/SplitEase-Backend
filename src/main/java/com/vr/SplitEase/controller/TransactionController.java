package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.response.AddTransactionResponse;
import com.vr.SplitEase.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/addTransaction")
    public ResponseEntity<AddTransactionResponse> addTransaction(@RequestBody @Valid AddTransactionRequest addTransactionRequest){
        AddTransactionResponse addTransactionResponse = transactionService.addTransaction(addTransactionRequest);
        return new ResponseEntity<>(addTransactionResponse, HttpStatus.OK);
    }
}
