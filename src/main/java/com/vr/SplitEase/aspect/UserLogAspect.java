package com.vr.SplitEase.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.SplitEase.config.constants.ActivityType;
import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.request.CreateUserRequest;
import com.vr.SplitEase.dto.request.SettleUpTransactionRequest;
import com.vr.SplitEase.dto.response.AddGroupResponse;
import com.vr.SplitEase.dto.response.AddTransactionResponse;
import com.vr.SplitEase.dto.response.CreateUserResponse;
import com.vr.SplitEase.dto.response.SettleUpTransactionResponse;
import com.vr.SplitEase.entity.*;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.GroupRepository;
import com.vr.SplitEase.repository.TransactionRepository;
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

import java.util.*;

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
    @Autowired
    private TransactionRepository transactionRepository;

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

    //settle up transaction log
    @Pointcut("execution(* com.vr.SplitEase.service.TransactionService.settleUpTransaction(..))")
    public void settleUpTransaction() {}

    //delete transaction log
    @Pointcut("execution(* com.vr.SplitEase.service.TransactionService.deleteTransaction(..))")
    public void deleteTransaction() {}

    // After the addUpdateGroup method returns, log the activity
    @AfterReturning(pointcut = "addUpdateGroup()", returning = "result")
    public void logAddUpdateGroupActivity(JoinPoint joinPoint, Object result) {
        String groupName = (String) joinPoint.getArgs()[0];
        AddGroupResponse addGroupResponse = (AddGroupResponse) result;
        //Log activity
        String currentUserUuid = currentUserService.getCurrentUserUuid().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("userUuid", currentUserUuid);
            logData.put("groupId", addGroupResponse.getGroupId());
            logData.put("description", String.format("You created a new group \"%s\".", groupName));

            ObjectMapper objectMapper = new ObjectMapper();
            String logJson = objectMapper.writeValueAsString(logData);
            userLogService.logActivity(currentUserUuid, ActivityType.ADD_GROUP, logJson);
        } catch (Exception e){
            e.printStackTrace();
        }
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
        try {
            //Log for all the users added
            for (User user: users){
                Map<String, Object> logData = new HashMap<>();
                logData.put("userUuid", currentUserUuid);
                logData.put("userName", userName);
                logData.put("groupId", group.getGroupId());
                logData.put("description", String.format("%s added You to \"%s\"", userName, group.getName()));

                ObjectMapper objectMapper = new ObjectMapper();
                String logJson = objectMapper.writeValueAsString(logData);
                userLogService.logActivity(user.getUserUuid(), ActivityType.ADD_USER_TO_GROUP, logJson);
            }
            //Log for the user who added
            for (User user: users){
                Map<String, Object> logData = new HashMap<>();
                logData.put("userUuid", currentUserUuid);
                logData.put("userName", userName);
                logData.put("groupId", group.getGroupId());
                logData.put("description", String.format("You added %s to \"%s\"", user.getName(), group.getName()));

                ObjectMapper objectMapper = new ObjectMapper();
                String logJson = objectMapper.writeValueAsString(logData);
                userLogService.logActivity(currentUserUuid, ActivityType.ADD_USER_TO_GROUP, logJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
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

        try {
            //Log for the user removed
            Map<String, Object> logData = new HashMap<>();
            logData.put("userUuid", currentUserUuid);
            logData.put("userName", userName);
            logData.put("groupId", group.getGroupId());
            logData.put("description", String.format("You were removed from \"%s\" by \"%s\"", group.getName(), userName));

            ObjectMapper objectMapper = new ObjectMapper();
            String logJson = objectMapper.writeValueAsString(logData);
            userLogService.logActivity(user.getUserUuid(), ActivityType.REMOVE_USER_FROM_GROUP, logJson);
            logData.clear();
            //Log for the user who removed
            logData.put("userUuid", currentUserUuid);
            logData.put("userName", userName);
            logData.put("groupId", group.getGroupId());
            logData.put("description", String.format("You removed \"%s\" from \"%s\"", user.getName(), group.getName()));

            String logJsonRemoved = objectMapper.writeValueAsString(logData);
            userLogService.logActivity(currentUserUuid, ActivityType.REMOVE_USER_FROM_GROUP, logJsonRemoved);
        } catch (Exception e){
            e.printStackTrace();
        }
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
        try {
            for (UserGroupLedger userGroupLedger : userGroupLedgers) {
                //Log for who deleted the group
                if (userGroupLedger.getUser().getUserUuid() == currentUserUuid) {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("groupId", group.getGroupId());
                    logData.put("description", String.format("You deleted \"%s\"", group.getName()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String logJson = objectMapper.writeValueAsString(logData);
                    userLogService.logActivity(currentUserUuid, ActivityType.DELETE_GROUP, logJson);
                } else { //Log for other members of the group
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("groupId", group.getGroupId());
                    logData.put("description", String.format("\"%s\" was deleted by \"%s\"", group.getName(), userName));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String logJson = objectMapper.writeValueAsString(logData);
                    userLogService.logActivity(userGroupLedger.getUser().getUserUuid(), ActivityType.DELETE_GROUP, logJson);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // After the addTransaction method returns, log the activity
    @AfterReturning(pointcut = "addTransaction()", returning = "result")
    public void logAddTransactionActivity(JoinPoint joinPoint, Object result) {
        AddTransactionRequest addTransactionRequest = (AddTransactionRequest) joinPoint.getArgs()[0];
        AddTransactionResponse addTransactionResponse = (AddTransactionResponse) result;

        User payingUser = userRepository.findById(addTransactionRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //Find the group
        Group group = groupRepository.findById(addTransactionRequest.getGroup()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Set<String> emails = addTransactionRequest.getUsersInvolved().keySet();
        List<User> users = new ArrayList<>();
        for (String email: emails){
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            users.add(user);
        }

        try {
            for (User user : users) {
                //Log for payer user
                if (Objects.equals(user.getUserUuid(), addTransactionRequest.getUserUuid())) {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("groupId", group.getGroupId());
                    logData.put("transactionId", addTransactionResponse.getTransactionId());
                    logData.put("description", String.format("You added \"%s\" in \"%s\"", addTransactionResponse.getDescription(), group.getName()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String logJson = objectMapper.writeValueAsString(logData);
                    userLogService.logActivity(user.getUserUuid(), ActivityType.ADD_TRANSACTION, logJson);
                } else { //Log for other users
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("groupId", group.getGroupId());
                    logData.put("transactionId", addTransactionResponse.getTransactionId());
                    logData.put("description", String.format("\"%s\" added \"%s\" in \"%s\"", payingUser.getName(), addTransactionResponse.getDescription(), group.getName()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String logJson = objectMapper.writeValueAsString(logData);
                    userLogService.logActivity(user.getUserUuid(), ActivityType.ADD_TRANSACTION, logJson);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // After the settleUpTransaction method returns, log the activity
    @AfterReturning(pointcut = "settleUpTransaction()", returning = "result")
    public void logSettleUpTransactionActivity(JoinPoint joinPoint, Object result) {
        SettleUpTransactionRequest settleUpTransactionRequest = (SettleUpTransactionRequest) joinPoint.getArgs()[0];
        SettleUpTransactionResponse settleUpTransactionResponse = (SettleUpTransactionResponse) result;

        User payingUser = userRepository.findById(settleUpTransactionRequest.getPayer()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User receivingUser = userRepository.findById(settleUpTransactionRequest.getReceiver()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            //Log for payer user
            Map<String, Object> logData = new HashMap<>();
            logData.put("settleId", settleUpTransactionResponse.getTransactionId());
            logData.put("description", String.format("You paid \"%s\" Rs. %.2f", receivingUser.getName(), settleUpTransactionResponse.getAmount()));
            ObjectMapper objectMapper = new ObjectMapper();
            String logJson = objectMapper.writeValueAsString(logData);
            userLogService.logActivity(payingUser.getUserUuid(), ActivityType.SETTLED, logJson);
            logData.clear();
            //Log for other users
            logData.put("settleId", settleUpTransactionResponse.getTransactionId());
            logData.put("description", String.format("\"%s\" paid You Rs. %.2f", payingUser.getName(), settleUpTransactionResponse.getAmount()));
            String logJsonOther = objectMapper.writeValueAsString(logData);
            userLogService.logActivity(receivingUser.getUserUuid(), ActivityType.SETTLED, logJsonOther);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // After the deleteTransaction method returns, log the activity
    @AfterReturning(pointcut = "deleteTransaction()", returning = "result")
    public void logDeleteTransactionActivity(JoinPoint joinPoint, Object result) {
        Integer transactionId = (Integer) joinPoint.getArgs()[0];

        String currentUserUuid = currentUserService.getCurrentUserUuid().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        String userName = currentUser.getName();

        //Find the transaction which is deleted
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        //Find the user ledger for the transaction
        Set<UserLedger> userLedgers = transaction.getUserLedger();
        try {
            for (UserLedger userLedger : userLedgers) {
                //Log for the user who deleted the transaction
                if (Objects.equals(userLedger.getUser().getUserUuid(), currentUserUuid)) {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("transactionId", transaction.getTransactionId());
                    logData.put("groupId", transaction.getGroup().getGroupId());
                    logData.put("description", String.format("You deleted \"%s\" from \"%s\"", transaction.getDescription(), transaction.getGroup().getName()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String logJson = objectMapper.writeValueAsString(logData);
                    userLogService.logActivity(currentUserUuid, ActivityType.DELETE_TRANSACTION, logJson);
                } else { //Log for other users
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("transactionId", transaction.getTransactionId());
                    logData.put("groupId", transaction.getGroup().getGroupId());
                    logData.put("description", String.format("\"%s\" deleted \"%s\" from \"%s\"", currentUser.getName(), transaction.getDescription(), transaction.getGroup().getName()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String logJson = objectMapper.writeValueAsString(logData);
                    userLogService.logActivity(userLedger.getUser().getUserUuid(), ActivityType.DELETE_TRANSACTION, logJson);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
