package com.vr.SplitEase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vr.SplitEase.config.constants.SplitBy;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetTransactionByGroupResponse {
    private Integer transactionId;
    private String description;
    private Double amount;
    private SplitBy splitBy;
    private Integer groupId;
    private String userUuid;
    private AddCategoryResponse category;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdOn;
    private LoggedInUserTransaction loggedInUserTransaction;
}
