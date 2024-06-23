package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddUserToGroupResponse;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.dto.response.DeleteResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

public interface GroupService {
    AddGroupResponse addUpdateGroup(AddGroupRequest addGroupRequest);
    AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest);
    ByteArrayInputStream generateExcelForGroupTransactions(Integer groupId) throws IOException;
    DeleteResponse removeUserFromGroup(Integer groupId, String userUuid);
    Set<CreateUserResponse> getGroupMembers(Integer groupId);
}
