package com.vr.SplitEase.service;

import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.request.SearchUserByEmailMobileRequest;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.dto.response.FriendsListResponse;
import com.vr.SplitEase.dto.response.GetTotalNetBalance;
import com.vr.SplitEase.dto.response.GetUserByUuidResponse;

import java.util.List;

public interface UserService {

    //Add Update user
    CreateUserResponse addUpdateUser(CreateUserRequest createUserRequest);

    //Delete User
    void deleteUser(String userUuid);

    //Get user by uuid
    GetUserByUuidResponse getUserByUuid(String userUuid);

    //Get user by email or mobile
    CreateUserResponse getUserByEmailOrMobile(SearchUserByEmailMobileRequest searchUserByEmailMobileRequest); //deferred for now

    GetTotalNetBalance getTotalNetBalanceByUserUuid(String userUuid, String searchVal);

    List<FriendsListResponse> userFriendsList(String userUuid);

    List<GetUserByUuidResponse> isUserExists(String userData);
}
