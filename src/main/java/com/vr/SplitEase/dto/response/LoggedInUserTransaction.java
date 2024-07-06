package com.vr.SplitEase.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoggedInUserTransaction {
    private String userUuid;
    private Double amount;
    private String owedOrLent;
}
