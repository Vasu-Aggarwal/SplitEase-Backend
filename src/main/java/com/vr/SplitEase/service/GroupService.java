package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;

public interface GroupService {
    AddGroupResponse addUpdateGroup(AddGroupRequest addGroupRequest);
}
