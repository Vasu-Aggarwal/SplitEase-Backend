package com.vr.SplitEase.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupSummaryResponse {
    private Integer groupId;
    private String groupName;
    private Double totalGroupSpending;
    private Double userPaidFor;
    private Double userTotalShare;
}
