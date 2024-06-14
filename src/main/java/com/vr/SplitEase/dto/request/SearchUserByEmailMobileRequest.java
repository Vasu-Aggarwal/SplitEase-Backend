package com.vr.SplitEase.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchUserByEmailMobileRequest {
    private String email;
    private String mobile;
}
