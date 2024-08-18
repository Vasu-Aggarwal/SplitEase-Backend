package com.vr.SplitEase.service;

import com.vr.SplitEase.config.constants.ActivityType;

public interface UserLogService {
    void logActivity(String userUuid, ActivityType activityType, String details);

}
