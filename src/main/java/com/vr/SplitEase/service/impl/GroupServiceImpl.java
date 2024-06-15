package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.config.constants.GroupStatus;
import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.GroupRepository;
import com.vr.SplitEase.service.GroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public AddGroupResponse addUpdateGroup(AddGroupRequest addGroupRequest) {
        Group group;
        if (addGroupRequest.getGroupId() != null){
            group = groupRepository.findById(addGroupRequest.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            group.setName(addGroupRequest.getName());
        } else {
            group = modelMapper.map(addGroupRequest, Group.class);
            group.setStatus(GroupStatus.ACTIVE.getStatus());
            group.setTotalAmount(0.0);
        }
        groupRepository.save(group);
        return modelMapper.map(group, AddGroupResponse.class);
    }
}
