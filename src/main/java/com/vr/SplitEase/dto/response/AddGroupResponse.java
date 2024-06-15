package com.vr.SplitEase.dto.response;

import com.vr.SplitEase.entity.UserGroupLedger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddGroupResponse {
    private Integer groupId;
    private String name;
    private Double totalAmount;
    private String status;
}
