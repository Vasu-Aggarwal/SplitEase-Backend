package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.request.SearchUserByEmailMobileRequest;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.entity.Role;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.RoleRepository;
import com.vr.SplitEase.repository.UserRepository;
import com.vr.SplitEase.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public CreateUserResponse addUpdateUser(CreateUserRequest createUserRequest) {
        User user = new User();
        //Update existing user if uuid is present in the request
        if (createUserRequest.getUserUuid() != null && !createUserRequest.getUserUuid().isBlank()){
            user = userRepository.findById(createUserRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            user.setEmail(createUserRequest.getEmail());
            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            user.setMobile(createUserRequest.getMobile());
            user.setName(createUserRequest.getName());
        } else {
            //Create new user
            //set role
            user = modelMapper.map(createUserRequest, User.class);
            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            Role role = this.roleRepository.findById(2).get();
            user.getRoles().add(role);
        }
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
    public CreateUserResponse getUserByEmailOrMobile(SearchUserByEmailMobileRequest searchUserByEmailMobileRequest) {
        User user;
        //if mobile and email both present
        if (!searchUserByEmailMobileRequest.getEmail().isBlank() && !searchUserByEmailMobileRequest.getMobile().isBlank()){
            //Give priority to email
            user = userRepository.findByEmail(searchUserByEmailMobileRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else if (!searchUserByEmailMobileRequest.getMobile().isBlank()){
            user = userRepository.findByMobile(searchUserByEmailMobileRequest.getMobile()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else if (!searchUserByEmailMobileRequest.getEmail().isBlank()){
            user = userRepository.findByEmail(searchUserByEmailMobileRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        } else {
            throw new BadApiRequestException("Email and Mobile both cannot be empty");
        }

        CreateUserResponse createUserResponse = modelMapper.map(user, CreateUserResponse.class);
        return createUserResponse;
    }
}
