package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.response.AddTransactionResponse;

public interface TransactionService {
    AddTransactionResponse addTransaction(AddTransactionRequest addTransactionRequest);
}
