package com.vr.SplitEase.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtResponse {

    private String token;
    private String refreshToken;
    private String userUuid;

}
