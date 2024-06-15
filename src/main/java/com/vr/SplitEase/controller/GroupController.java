package com.vr.SplitEase.controller;

import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddUserToGroupResponse;
import com.vr.SplitEase.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/addUpdateGroup")
    public ResponseEntity<AddGroupResponse> addUpdateGroup(@RequestBody @Valid AddGroupRequest addGroupRequest){
        AddGroupResponse addGroupResponse = groupService.addUpdateGroup(addGroupRequest);
        return new ResponseEntity<>(addGroupResponse, HttpStatus.OK);
    }

    @PostMapping("/addUsersToGroup")
    public ResponseEntity<AddUserToGroupResponse> addUserToGroup(@RequestBody @Valid AddUserToGroupRequest addUserToGroupRequest){
        AddUserToGroupResponse addUserToGroupResponse = groupService.addUsersToGroup(addUserToGroupRequest);
        return new ResponseEntity<>(addUserToGroupResponse, HttpStatus.OK);
    }
}
