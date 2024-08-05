package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum AppConstants {
    EMAIL_FROM_ADDRESS("split.ease01@gmail.com"),
    ALL_GROUPS("allGroups"),
    GROUPS_THAT_OWE_YOU("groupsThatOweYou"),
    GROUPS_YOU_OWE("groupsYouOwe"),
    OUTSTANDING_BALANCE("outstandingBalance");

    private final String value;

    AppConstants(String value) {
        this.value = value;
    }
}
