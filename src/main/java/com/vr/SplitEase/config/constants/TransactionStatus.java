package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    ACTIVE(1),
    DELETED(0);

    private final int status;

    TransactionStatus(int status) {
        this.status = status;
    }
}
