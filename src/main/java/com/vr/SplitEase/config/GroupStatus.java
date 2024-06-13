package com.vr.SplitEase.config;

import lombok.Getter;

@Getter
public enum GroupStatus {
    ACTIVE(1),
    INACTIVE(2),
    DELETED(3);

    private final Integer status;
    GroupStatus(Integer status) {
        this.status = status;
    }
}
