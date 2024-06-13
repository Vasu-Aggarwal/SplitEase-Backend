package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum GroupStatus {
    ACTIVE(1),
    INACTIVE(0),
    DELETED(3);

    private final Integer status;
    GroupStatus(Integer status) {
        this.status = status;
    }
}
