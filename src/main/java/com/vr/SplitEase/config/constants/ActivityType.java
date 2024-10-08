package com.vr.SplitEase.config.constants;

import lombok.Getter;

@Getter
public enum ActivityType {
    LOGIN,
    LOGOUT,
    SERVICE_CALL,
    SERVICE_CALL_COMPLETED,
    ADD_TRANSACTION,
    DELETE_TRANSACTION,
    UPDATE_TRANSACTION,
    ADD_GROUP,
    DELETE_GROUP,
    UPDATE_GROUP,
    REGISTER_USER,
    ADD_USER_TO_GROUP,
    REMOVE_USER_FROM_GROUP,
    EXPORT_TRANSACTION,
    SETTLED;
}
