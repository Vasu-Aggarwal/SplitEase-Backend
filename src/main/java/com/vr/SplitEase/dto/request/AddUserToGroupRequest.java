package com.vr.SplitEase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddUserToGroupRequest {

    @NotNull(message = "User uuid cannot be null")
    @NotBlank(message = "User uuid cannot be Empty")
    private String userUuid;

    @Size(min = 1)
    private Set<String> userList = new HashSet<>();

    @NotNull(message = "Group id cannot be null")
    private Integer groupId;
}
