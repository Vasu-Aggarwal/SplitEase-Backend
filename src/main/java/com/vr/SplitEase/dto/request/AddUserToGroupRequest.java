package com.vr.SplitEase.dto.request;

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

    @Size(min = 1)
    private Set<String> userList = new HashSet<>();

    @NotNull(message = "Group id cannot be null")
    private Integer groupId;
}
