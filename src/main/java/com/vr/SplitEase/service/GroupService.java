package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.*;
import jakarta.mail.MessagingException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface GroupService {
    AddGroupResponse addUpdateGroup(AddGroupRequest addGroupRequest);
    AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest) throws MessagingException;
    ByteArrayInputStream generateExcelForGroupTransactions(Integer groupId) throws IOException;
    DeleteResponse removeUserFromGroup(Integer groupId, String userUuid);
    Set<CreateUserResponse> getGroupMembers(Integer groupId);
    Set<GetGroupMembersV2Response> getGroupMembersV2(Integer groupId);
    List<AddGroupResponse> getGroupsByUserUuid(String userUuid);
}
