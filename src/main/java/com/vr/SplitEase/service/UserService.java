package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.request.SearchUserByEmailMobileRequest;
import com.vr.SplitEase.dto.response.CreateUserResponse;

public interface UserService {

    //Add Update user
    CreateUserResponse addUpdateUser(CreateUserRequest createUserRequest);

    //Delete User
    void deleteUser(String userUuid);

    //Get user by uuid
    CreateUserResponse getUserByUuid(String userUuid);

    //Get user by email or mobile
    CreateUserResponse getUserByEmailOrMobile(SearchUserByEmailMobileRequest);
}
