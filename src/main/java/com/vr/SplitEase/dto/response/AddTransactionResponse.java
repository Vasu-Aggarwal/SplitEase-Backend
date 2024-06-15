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
public class AddTransactionResponse {
    private Integer transactionId;
    private Double amount;
    private SplitBy splitBy;
    private Integer groupId;
    private String userUuid;
    private String category;
}
