package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.*;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface GroupService {
    AddGroupResponse addUpdateGroup(String name, Integer groupId, MultipartFile image);
    AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest) throws MessagingException;
    ByteArrayInputStream generateExcelForGroupTransactions(Integer groupId) throws IOException;
    DeleteResponse removeUserFromGroup(Integer groupId, String userUuid);
    List<CreateUserResponse> getGroupMembers(Integer groupId);
    List<GetGroupMembersV2Response> getGroupMembersV2(Integer groupId);
    List<GetGroupsByUserResponse> getGroupsByUserUuid(String userUuid);
    GroupSummaryResponse getGroupSpendingSummary(Integer groupId);
    AddGroupResponse getGroupInfo(Integer groupId);
}
