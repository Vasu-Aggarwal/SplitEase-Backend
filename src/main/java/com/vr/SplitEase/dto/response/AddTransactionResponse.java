package com.vr.SplitEase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vr.SplitEase.config.constants.SplitBy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddTransactionResponse {
    private Integer transactionId;
    private String description;
    private Double amount;
    private SplitBy splitBy;
    private Integer groupId;
    private String userUuid;
    private AddCategoryResponse category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdOn;
}
