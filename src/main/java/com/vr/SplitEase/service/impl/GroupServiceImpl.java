package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.config.constants.GroupStatus;
import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddUserToGroupResponse;
import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.UserGroupLedger;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.GroupRepository;
import com.vr.SplitEase.repository.UserGroupLedgerRepository;
import com.vr.SplitEase.service.GroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupLedgerRepository userGroupLedgerRepository;


    @Override
    public AddGroupResponse addUpdateGroup(AddGroupRequest addGroupRequest) {
        Group group;
        if (addGroupRequest.getGroupId() != null){
            group = groupRepository.findById(addGroupRequest.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            //Allow update of active state groups.
            if (Objects.equals(group.getStatus(), GroupStatus.ACTIVE.getStatus())){
                group.setName(addGroupRequest.getName());
            } else {
                throw new BadApiRequestException("Cannot update inactive group.");
            }
        } else {
            group = modelMapper.map(addGroupRequest, Group.class);
            group.setStatus(GroupStatus.ACTIVE.getStatus());
            group.setTotalAmount(0.0);

            //Add the user (who is creating the group) to the group
            UserGroupLedger userGroupLedger = new UserGroupLedger();
//            userGroupLedger.setUser();
        }
        groupRepository.save(group);
        return modelMapper.map(group, AddGroupResponse.class);
    }

    @Override
    public AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest) {
        return null;
    }
}
