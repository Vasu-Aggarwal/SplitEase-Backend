package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.request.SettleUpTransactionRequest;
import com.vr.SplitEase.dto.response.*;
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
import java.util.List;

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

    @PostMapping("/updateTransaction")
    public ResponseEntity<AddTransactionResponse> updateTransaction(@RequestBody @Valid AddTransactionRequest addTransactionRequest){
        AddTransactionResponse addTransactionResponse = transactionService.updateTransaction(addTransactionRequest);
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

    @GetMapping("/getTransactionById/{transactionId}")
    public ResponseEntity<GetTransactionByIdResponse> getTransactionById(@PathVariable Integer transactionId){
        GetTransactionByIdResponse getTransactionByIdResponse = transactionService.getTransactionById(transactionId);
        return new ResponseEntity<>(getTransactionByIdResponse, HttpStatus.OK);
    }

    @GetMapping("/getTransactionsByGroup/{groupId}")
    public ResponseEntity<List<GetTransactionByGroupResponse>> getTransactionsByGroup(@PathVariable Integer groupId){
        List<GetTransactionByGroupResponse> addTransactionResponses = transactionService.getTransactionsByGroupId(groupId);
        return new ResponseEntity<>(addTransactionResponses, HttpStatus.OK);
    }

    @GetMapping("/getTransactionsByUser/{userUuid}")
    public ResponseEntity<List<GetTransactionByGroupResponse>> getTransactionsByUser(@PathVariable String userUuid){
        List<GetTransactionByGroupResponse> addTransactionResponses = transactionService.getTransactionsByUser(userUuid);
        return new ResponseEntity<>(addTransactionResponses, HttpStatus.OK);
    }

    @PostMapping("/restoreTransaction/{transactionId}")
    public ResponseEntity<GetTransactionByIdResponse> restoreTransaction(@PathVariable Integer transactionId){
        GetTransactionByIdResponse addTransactionResponses = transactionService.restoreTransactionById(transactionId);
        return new ResponseEntity<>(addTransactionResponses, HttpStatus.OK);
    }
}
