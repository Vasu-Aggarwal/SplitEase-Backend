package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.request.SettleUpTransactionRequest;
import com.vr.SplitEase.dto.response.*;
import com.vr.SplitEase.entity.Group;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface TransactionService {
    AddTransactionResponse addTransaction(AddTransactionRequest addTransactionRequest);
    AddTransactionResponse updateTransaction(AddTransactionRequest addTransactionRequest);
    CalculatedDebtResponse calculateDebt(Integer groupId);
    SettleUpTransactionResponse settleUpTransaction(SettleUpTransactionRequest settleUpTransactionRequest);
    DeleteResponse deleteTransaction(Integer transactionId);
    GetTransactionByIdResponse getTransactionById(Integer transactionId);
    List<GetTransactionByGroupResponse> getTransactionsByGroupId(Integer groupId);
    List<GetTransactionByGroupResponse> getTransactionsByUser(String userUuid);
    GetTransactionByIdResponse restoreTransactionById(Integer transactionId);
}
