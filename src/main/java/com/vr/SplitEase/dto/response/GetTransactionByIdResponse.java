package com.vr.SplitEase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vr.SplitEase.config.constants.SplitBy;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionByIdResponse {
    private Integer transactionId;
    private String description;
    private Double amount;
    private SplitBy splitBy;
    private Integer groupId;
    private String userUuid;
    private AddCategoryResponse category;
    private List<UserLedgerDetails> userLedgerDetails = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdOn;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLedgerDetails{
        private String userUuid;
        private String name;
        private Double amount;
        private String owedOrLent;
    }
}
