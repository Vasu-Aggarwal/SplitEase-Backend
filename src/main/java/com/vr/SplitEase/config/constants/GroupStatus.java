package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum GroupStatus {
    ACTIVE(1),
    INACTIVE(0),
    DELETED(2);

    private final int status;

    GroupStatus(int status) {
        this.status = status;
    }
}
