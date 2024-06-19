package com.vr.SplitEase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SettleUpTransactionRequest {

    @NotNull(message = "Payer Id cannot be null")
    @NotBlank(message = "Payer Id cannot be empty")
    private String payer;
    @NotNull(message = "Receiver Id cannot be null")
    @NotBlank(message = "Receiver Id cannot be empty")
    private String receiver;
    @NotNull(message = "Amount cannot be null")
    private Double amount;
    @NotNull(message = "Group Id cannot be null")
    private Integer groupId;
}
