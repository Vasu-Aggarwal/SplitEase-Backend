package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum SplitBy {

    EQUAL("EQUAL"),
    UNEQUAL("UNEQUAL");

    private final String splitBy;
    SplitBy(String splitBy){
        this.splitBy = splitBy;
    }
}
