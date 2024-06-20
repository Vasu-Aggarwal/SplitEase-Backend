package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.request.SettleUpTransactionRequest;
import com.vr.SplitEase.dto.response.AddTransactionResponse;
import com.vr.SplitEase.dto.response.CalculatedDebtResponse;
import com.vr.SplitEase.dto.response.DeleteResponse;
import com.vr.SplitEase.dto.response.SettleUpTransactionResponse;
import com.vr.SplitEase.service.TransactionService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/settleUp")
    public ResponseEntity<SettleUpTransactionResponse> settleUpTransactionResponseResponseEntity(@RequestBody @Valid SettleUpTransactionRequest settleUpTransactionRequest){
        SettleUpTransactionResponse settleUpTransactionResponse = transactionService.settleUpTransaction(settleUpTransactionRequest);
        return new ResponseEntity<>(settleUpTransactionResponse, HttpStatus.OK);
    }

    @GetMapping("/calculateDebt/{groupId}")
    public ResponseEntity<CalculatedDebtResponse> calculatedDebtResponseResponseEntity(@PathVariable Integer groupId){
        CalculatedDebtResponse calculatedDebtResponse = transactionService.calculateDebt(groupId);
        return new ResponseEntity<>(calculatedDebtResponse, HttpStatus.OK);
    }

    @DeleteMapping("/deleteTransaction/{transactionId}")
    public ResponseEntity<DeleteResponse> deleteTransaction(@PathVariable Integer transactionId){
        DeleteResponse deleteResponse = transactionService.deleteTransaction(transactionId);
        return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
    }
}