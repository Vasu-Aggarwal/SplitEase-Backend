package com.vr.SplitEase.dto.response;

import com.vr.SplitEase.config.constants.SplitBy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SettleUpTransactionResponse {
    private Integer transactionId;
    private Double amount;
    private Integer groupId;
    private String payer;
    private String receiver;
}
