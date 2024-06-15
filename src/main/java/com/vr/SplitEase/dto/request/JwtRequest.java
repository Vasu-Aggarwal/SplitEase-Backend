package com.vr.SplitEase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtRequest {

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be empty")
    private String password;

}
