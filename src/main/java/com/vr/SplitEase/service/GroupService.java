package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddUserToGroupResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface GroupService {
    AddGroupResponse addUpdateGroup(AddGroupRequest addGroupRequest);
    AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest);
    ByteArrayInputStream generateExcelForGroupTransactions(Integer groupId) throws IOException;
}
