package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.response.AddTransactionResponse;
import com.vr.SplitEase.entity.Group;

public interface TransactionService {
    AddTransactionResponse addTransaction(AddTransactionRequest addTransactionRequest);
    void calculateDebt(Group groupId);
}
