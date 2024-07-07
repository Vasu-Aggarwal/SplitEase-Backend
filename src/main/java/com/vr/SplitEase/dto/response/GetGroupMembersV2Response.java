package com.vr.SplitEase.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetGroupMembersV2Response {
    private String name;
    private String userUuid;
    private String email;
}
