package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.response.CreateUserResponse;

public interface UserService {

    //Add Update user
    CreateUserResponse addUpdateUser(CreateUserRequest createUserRequest);

    //Delete User

    //Get user by uuid

    //Get user by email or mobile
}
