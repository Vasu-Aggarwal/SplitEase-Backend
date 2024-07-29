package com.vr.SplitEase.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetGroupsByUserResponse {
    private Integer groupId;
    private String name;
    private String status;
    private String imageUrl;
    private Double userBalance;
}
