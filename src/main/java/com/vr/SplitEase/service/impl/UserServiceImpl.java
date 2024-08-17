package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.config.constants.AppConstants;
import com.vr.SplitEase.config.constants.GroupStatus;
import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.request.SearchUserByEmailMobileRequest;
import com.vr.SplitEase.dto.response.*;
import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.Role;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserGroupLedger;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.RoleRepository;
import com.vr.SplitEase.repository.UserGroupLedgerRepository;
import com.vr.SplitEase.repository.UserRepository;
import com.vr.SplitEase.service.GroupService;
import com.vr.SplitEase.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserGroupLedgerRepository userGroupLedgerRepository;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserGroupLedgerRepository userGroupLedgerRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userGroupLedgerRepository = userGroupLedgerRepository;
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
            if (!createUserRequest.getEmail().isBlank() || !createUserRequest.getMobile().isBlank()){
                user = modelMapper.map(createUserRequest, User.class);
                if (createUserRequest.getMobile().isBlank()){
                    user.setMobile(null);
                } else{
                    user.setEmail(null);
                }
                if (createUserRequest.getPassword().isBlank()){
                    //set the default password
                    user.setPassword(passwordEncoder.encode(AppConstants.DEFAULT_PASSWORD.getValue()));
                } else {
                    user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
                }
                Role role = this.roleRepository.findById(2).get();
                user.getRoles().add(role);
            } else {
                throw new BadApiRequestException("Email or Mobile should not be empty");
            }
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
    public GetUserByUuidResponse getUserByUuid(String userUuid) {
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        GetUserByUuidResponse getUserByUuidResponse = modelMapper.map(user, GetUserByUuidResponse.class);
        return getUserByUuidResponse;
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

    @Override
    public GetTotalNetBalance getTotalNetBalanceByUserUuid(String userUuid, String searchVal) {
        //find the user by uuid
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        double totalNetBalance = 0.0;
        if (searchVal.equalsIgnoreCase(AppConstants.ALL_GROUPS.getValue())){
            totalNetBalance = user.getUserGroups().stream().mapToDouble(UserGroupLedger::getNetBalance).sum();
        } else if (searchVal.equalsIgnoreCase(AppConstants.GROUPS_YOU_OWE.getValue())){
            totalNetBalance = user.getUserGroups().stream().filter(userGroupLedger -> userGroupLedger.getNetBalance()>0).mapToDouble(UserGroupLedger::getNetBalance).sum();
        } else if (searchVal.equalsIgnoreCase(AppConstants.GROUPS_THAT_OWE_YOU.getValue())){
            totalNetBalance = user.getUserGroups().stream().filter(userGroupLedger -> userGroupLedger.getNetBalance()<0).mapToDouble(UserGroupLedger::getNetBalance).sum();
        } else if (searchVal.equalsIgnoreCase(AppConstants.OUTSTANDING_BALANCE.getValue())){
            totalNetBalance = user.getUserGroups().stream().filter(userGroupLedger -> userGroupLedger.getNetBalance()!=0).mapToDouble(UserGroupLedger::getNetBalance).sum();
        }
        return GetTotalNetBalance.builder().netBalance(totalNetBalance).build();
    }

    @Override
    public List<FriendsListResponse> userFriendsList(String userUuid) {
        //Find the user by uuid
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));


        //Get the list of groups the user is part of
        List<UserGroupLedger> userGroupLedgers = userGroupLedgerRepository.findByUserAndStatus(user, GroupStatus.ACTIVE.getStatus()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));

        List<Group> groups = userGroupLedgers.stream().map(UserGroupLedger::getGroup).toList();

        List<CreateUserResponse> userResponseList = new ArrayList<>();

        groups.stream().map(group ->
            group.getUserGroups().stream().map(userGroupLedger ->
                    userResponseList.add(modelMapper.map(userGroupLedger.getUser(), CreateUserResponse.class))
            )
        );
        log.info("USERS FRIENDS ARE: "+userResponseList);

        Set<CreateUserResponse> userList = groups.get(0).getUserGroups().stream().map(userGroupLedger ->
                modelMapper.map(userGroupLedger.getUser(), CreateUserResponse.class)
        ).collect(Collectors.toSet());

        //find all the users which are part of the groups where logged-in user is added

        return null;
    }

    @Override
    public List<GetUserByUuidResponse> isUserExists(String userData) {
        List<User> users = userRepository.findByNameOrEmailOrMobileIgnoreCase(userData).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<GetUserByUuidResponse> response = users.stream().map(user -> modelMapper.map(user, GetUserByUuidResponse.class)).toList();
        return response;
    }
}
