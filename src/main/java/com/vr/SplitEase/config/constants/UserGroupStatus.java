package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum UserGroupStatus {

    ACTIVE(1),
    INACTIVE(0),
    DELETED(3);

    private final Integer status;
    UserGroupStatus(Integer status){
        this.status = status;
    }

}
