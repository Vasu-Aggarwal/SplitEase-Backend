package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.config.constants.ActivityType;
import com.vr.SplitEase.entity.UserLogs;
import com.vr.SplitEase.repository.UserLogRepository;
import com.vr.SplitEase.service.UserLogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLogServiceImpl implements UserLogService {

    @Autowired
    private ModelMapper modelMapper;

    private final UserLogRepository userLogRepository;

    public UserLogServiceImpl(UserLogRepository userLogRepository) {
        this.userLogRepository = userLogRepository;
    }


    @Override
    public void logActivity(String userUuid, ActivityType activityType, String details) {
        UserLogs userLogs = new UserLogs();
        userLogs.setUserUuid(userUuid);
        userLogs.setActivityType(activityType);
        userLogs.setDetails(details);
        userLogRepository.save(userLogs);
    }
}
