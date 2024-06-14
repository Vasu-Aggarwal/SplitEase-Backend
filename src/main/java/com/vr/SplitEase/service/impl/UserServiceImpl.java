package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.UserRepository;
import com.vr.SplitEase.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CreateUserResponse addUpdateUser(CreateUserRequest createUserRequest) {
        User user = modelMapper.map(createUserRequest, User.class);
        userRepository.save(user);
        return modelMapper.map(user, CreateUserResponse.class);
    }

    @Override
    public void deleteUser(String userUuid) {
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public CreateUserResponse getUserByUuid(String userUuid) {
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CreateUserResponse createUserResponse = modelMapper.map(user, CreateUserResponse.class);
        return createUserResponse;
    }

    @Override
    public CreateUserResponse getUserByEmailOrMobile(String email, String mobile) {

    }
}
