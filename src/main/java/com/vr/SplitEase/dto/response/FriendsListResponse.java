package com.vr.SplitEase.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FriendsListResponse {
    private String name;
    private String userUuid;
    private Double netBalance;
}
