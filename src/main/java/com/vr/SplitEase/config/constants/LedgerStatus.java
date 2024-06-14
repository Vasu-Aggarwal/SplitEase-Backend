package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum LedgerStatus {
    ACTIVE(1),
    INACTIVE(0),
    DELETED(3),
    SETTLED(2);

    private final Integer status;
    LedgerStatus(Integer status) {
        this.status = status;
    }
}
