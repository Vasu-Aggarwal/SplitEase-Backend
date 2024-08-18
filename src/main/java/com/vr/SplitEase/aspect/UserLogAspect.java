package com.vr.SplitEase.aspect;

import com.vr.SplitEase.config.constants.ActivityType;
import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddTransactionResponse;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserGroupLedger;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.GroupRepository;
import com.vr.SplitEase.repository.UserRepository;
import com.vr.SplitEase.service.UserLogService;
import com.vr.SplitEase.service.impl.CurrentUserService;
import org.apache.commons.math3.analysis.function.Add;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Aspect
@Component
public class UserLogAspect {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    //Pointcut for the addUpdateGroup method
    @Pointcut("execution(* com.vr.SplitEase.service.GroupService.addUpdateGroup(..))")
    public void addUpdateGroup() {}

    //Add users to group log
    @Pointcut("execution(* com.vr.SplitEase.service.GroupService.addUsersToGroup(..))")
    public void addUsersToGroup() {}

    //Remove user from group log
    @Pointcut("execution(* com.vr.SplitEase.service.GroupService.removeUserFromGroup(..))")
    public void removeUserFromGroup() {}

    //delete group log
    @Pointcut("execution(* com.vr.SplitEase.service.GroupService.removeUserFromGroup(..))")
    public void deleteGroup() {}

    //add transaction log
    @Pointcut("execution(* com.vr.SplitEase.service.TransactionService.addTransaction(..))")
    public void addTransaction() {}

    // After the addUpdateGroup method returns, log the activity
    @AfterReturning(pointcut = "addUpdateGroup()", returning = "result")
    public void logAddUpdateGroupActivity(JoinPoint joinPoint, Object result) {
        String groupName = (String) joinPoint.getArgs()[0];
        //Log activity
        String currentUserUuid = currentUserService.getCurrentUserUuid().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        String description = String.format("You created a new group \"%s\".", groupName);
        userLogService.logActivity(currentUserUuid, ActivityType.ADD_GROUP, description);
    }

    // After the addUserToGroup method returns, log the activity
    @AfterReturning(pointcut = "addUsersToGroup()", returning = "result")
    public void logAddUsersToGroupActivity(JoinPoint joinPoint, Object result) {
        AddUserToGroupRequest request = (AddUserToGroupRequest) joinPoint.getArgs()[0];
        //Log activity
        String currentUserUuid = currentUserService.getCurrentUserUuid().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        String userName = currentUser.getName();
        //Find the group
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        //Find the users which were added
        List<User> users = request.getUserList().stream().map(user -> userRepository.findByEmail(user).orElseThrow(() -> new ResourceNotFoundException("User not found"))).toList();
        //Log for all the users added
        for (User user: users){
            String description = String.format("%s added You to \"%s\"", userName, group.getName());
            userLogService.logActivity(user.getUserUuid(), ActivityType.ADD_GROUP, description);
        }
        //Log for the user who added
        for (User user: users){
            String description = String.format("You added %s to \"%s\"", user.getName(), group.getName());
            userLogService.logActivity(user.getUserUuid(), ActivityType.ADD_GROUP, description);
        }
    }

    // After the addUserToGroup method returns, log the activity
    @AfterReturning(pointcut = "removeUserFromGroup()", returning = "result")
    public void logRemoveUserFromGroupActivity(JoinPoint joinPoint, Object result) {
        Integer groupId = (Integer) joinPoint.getArgs()[0];
        String removedUser = (String) joinPoint.getArgs()[1];
        User user = userRepository.findById(removedUser).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        //Log activity
        String currentUserUuid = currentUserService.getCurrentUserUuid().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        String userName = currentUser.getName();
        //Find the group
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        //Log for the user removed
        String description = String.format("You were removed from \"%s\" by \"%s\"", group.getName(), userName);
        userLogService.logActivity(user.getUserUuid(), ActivityType.REMOVE_USER_FROM_GROUP, description);

        //Log for the user who removed
        String descriptionRemover = String.format("You removed \"%s\" from \"%s\"", user.getName(), group.getName());
        userLogService.logActivity(currentUserUuid, ActivityType.REMOVE_USER_FROM_GROUP, descriptionRemover);

    }

    // After the deleteGroup method returns, log the activity
    @AfterReturning(pointcut = "deleteGroup()", returning = "result")
    public void logDeleteGroupActivity(JoinPoint joinPoint, Object result) {
        Integer groupId = (Integer) joinPoint.getArgs()[0];
        String userDeletedGroup = (String) joinPoint.getArgs()[1];
        User user = userRepository.findById(userDeletedGroup).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        //Log activity
        String currentUserUuid = currentUserService.getCurrentUserUuid().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        String userName = currentUser.getName();
        //Find the group
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        List<UserGroupLedger> userGroupLedgers = (List<UserGroupLedger>) group.getUserGroups();
        for (UserGroupLedger userGroupLedger: userGroupLedgers){
            //Log for who deleted the group
            if (userGroupLedger.getUser().getUserUuid() == currentUserUuid){
                String description = String.format("You deleted \"%s\"", group.getName());
                userLogService.logActivity(currentUserUuid, ActivityType.REMOVE_USER_FROM_GROUP, description);
            } else { //Log for other members of the group
                String description = String.format("\"%s\" was deleted by \"%s\"", group.getName(), userName);
                userLogService.logActivity(userGroupLedger.getUser().getUserUuid(), ActivityType.REMOVE_USER_FROM_GROUP, description);
            }
        }
    }

    // After the addTransaction method returns, log the activity
    @AfterReturning(pointcut = "addTransaction()", returning = "result")
    public void logAddTransactionActivity(JoinPoint joinPoint, Object result) {
        AddTransactionRequest addTransactionRequest = (AddTransactionRequest) joinPoint.getArgs()[0];
        AddTransactionResponse addTransactionResponse = (AddTransactionResponse) result;

        User payingUser = userRepository.findByEmail(addTransactionRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //Find the group
        Group group = groupRepository.findById(addTransactionRequest.getGroup()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Set<String> emails = addTransactionRequest.getUsersInvolved().keySet();
        List<User> users = new ArrayList<>();
        for (String email: emails){
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            users.add(user);
        }

        for (User user: users){
            //Log for payer user
            if (Objects.equals(user.getUserUuid(), addTransactionRequest.getUserUuid())){
                String description = String.format("You added \"%s\" in \"%s\"", addTransactionResponse.getDescription(), group.getName());
                userLogService.logActivity(user.getUserUuid(), ActivityType.REMOVE_USER_FROM_GROUP, description);
            } else { //Log for other users
                String description = String.format("\"%s\" added \"%s\" in \"%s\"", payingUser.getName(), addTransactionResponse.getDescription(), group.getName());
                userLogService.logActivity(user.getUserUuid(), ActivityType.REMOVE_USER_FROM_GROUP, description);
            }
        }
    }
}
