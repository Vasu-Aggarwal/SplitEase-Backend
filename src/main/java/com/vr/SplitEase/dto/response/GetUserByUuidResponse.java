package com.vr.SplitEase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetUserByUuidResponse {
    private String name;
    private String userUuid;
    private String email;
}
