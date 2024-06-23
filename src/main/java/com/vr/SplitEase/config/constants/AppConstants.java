package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum AppConstants {
    EMAIL_FROM_ADDRESS("split.ease01@gmail.com")
    ;

    private final String value;

    AppConstants(String value) {
        this.value = value;
    }
}
