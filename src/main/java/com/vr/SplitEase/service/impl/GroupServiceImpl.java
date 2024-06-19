package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.config.constants.GroupStatus;
import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddUserToGroupResponse;
import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserGroupLedger;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.GroupRepository;
import com.vr.SplitEase.repository.UserGroupLedgerRepository;
import com.vr.SplitEase.repository.UserRepository;
import com.vr.SplitEase.service.GroupService;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;
    private final UserGroupLedgerRepository userGroupLedgerRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public GroupServiceImpl(ModelMapper modelMapper, GroupRepository groupRepository, UserGroupLedgerRepository userGroupLedgerRepository, CurrentUserService currentUserService, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.groupRepository = groupRepository;
        this.userGroupLedgerRepository = userGroupLedgerRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
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
            groupRepository.save(group);
        } else {
            group = modelMapper.map(addGroupRequest, Group.class);
            group.setStatus(GroupStatus.ACTIVE.getStatus());
            group.setTotalAmount(0.0);
            groupRepository.save(group);
            //Add the user (who is creating the group) to the group
            UserGroupLedger userGroupLedger = new UserGroupLedger();
            userGroupLedger.setUser(currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong")));
            userGroupLedger.setGroup(group);
            userGroupLedger.setStatus(GroupStatus.ACTIVE.getStatus());
            userGroupLedger.setTotalOwed(0.00);
            userGroupLedger.setTotalLent(0.00);
            userGroupLedger.setNetBalance(0.00);
            userGroupLedgerRepository.save(userGroupLedger);
        }
        return modelMapper.map(group, AddGroupResponse.class);
    }

    @Override
    @Transactional
    public AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest) {
        Set<User> users = addUserToGroupRequest.getUserList().stream().map(userEmail ->
                userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException(String.format("User with email: %s not found", userEmail))))
                .collect(Collectors.toSet());
        Group group = groupRepository.findById(addUserToGroupRequest.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        //check whether no user present in the list is already present with same group
        List<String> conflictingUsers = new ArrayList<>();
        for (User user : users) {
            if (userGroupLedgerRepository.existsByUserAndGroup(user, group)) {
                conflictingUsers.add(user.getEmail());
            }
        }
        if (!conflictingUsers.isEmpty()) {
            throw new BadApiRequestException("Users already part of the group: " + String.join(", ", conflictingUsers));
        }

        try {
            for (User user : users) {
                UserGroupLedger userGroupLedger = new UserGroupLedger();
                userGroupLedger.setUser(user);
                userGroupLedger.setGroup(group);
                userGroupLedger.setStatus(GroupStatus.ACTIVE.getStatus());
                userGroupLedger.setTotalLent(0.00);
                userGroupLedger.setTotalOwed(0.00);
                userGroupLedger.setNetBalance(0.00);
                userGroupLedgerRepository.save(userGroupLedger);
            }
        } catch (Exception e){
            throw new BadApiRequestException(e.getMessage());
        }
        AddUserToGroupResponse addUserToGroupResponse = new AddUserToGroupResponse("Users added successfully");
        return addUserToGroupResponse;
    }
}
