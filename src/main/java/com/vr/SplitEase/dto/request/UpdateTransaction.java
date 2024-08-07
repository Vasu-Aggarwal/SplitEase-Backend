package com.vr.SplitEase.dto.request;

import com.vr.SplitEase.config.constants.SplitBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTransaction {
    @NotNull(message = "Transaction Id cannot be null")
    private Integer transactionId;
    @Positive(message = "Amount must be greater than zero")
    private Double amount;
    @NotNull(message = "Split by cannot be null")
    private SplitBy splitBy;
    @NotNull(message = "Group id cannot be null")
    private Integer group;
    @NotNull(message = "User uuid cannot be null")
    @NotBlank(message = "User uuid cannot be empty")
    private String userUuid;
    @NotNull(message = "Category cannot be null")
    @NotBlank(message = "Category cannot be empty")
    private String category;
    @NotNull(message = "Users involved list cannot be null")
    @Size(min = 1, message = "At least one user should be selected")
    private Map<String, Double> usersInvolved = new HashMap<>();
    @NotBlank(message = "Description cannot be empty")
    private String description;
}
