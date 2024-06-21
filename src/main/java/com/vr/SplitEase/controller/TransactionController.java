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
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

    @GetMapping("/export/{groupId}")
    public ResponseEntity<InputStreamResource> exportExcel(@PathVariable Integer groupId) throws IOException {
        ByteArrayInputStream in = transactionService.generateExcelForGroupTransactions(groupId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }
}
