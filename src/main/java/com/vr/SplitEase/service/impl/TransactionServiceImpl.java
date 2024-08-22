package com.vr.SplitEase.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.SplitEase.config.constants.*;
import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.request.SettleUpTransactionRequest;
import com.vr.SplitEase.dto.response.*;
import com.vr.SplitEase.entity.*;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.*;
import com.vr.SplitEase.service.RedisService;
import com.vr.SplitEase.service.TransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final SubCategoryRepository categoryRepository;
    private final UserGroupLedgerRepository userGroupLedgerRepository;
    private final UserLedgerRepository userLedgerRepository;
    private final CurrentUserService currentUserService;
    private final RedisService redisService;

    public TransactionServiceImpl(ModelMapper modelMapper, TransactionRepository transactionRepository, UserRepository userRepository, GroupRepository groupRepository, SubCategoryRepository categoryRepository, UserGroupLedgerRepository userGroupLedgerRepository, UserLedgerRepository userLedgerRepository, CurrentUserService currentUserService, RedisService redisService) {
        this.modelMapper = modelMapper;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
        this.userGroupLedgerRepository = userGroupLedgerRepository;
        this.userLedgerRepository = userLedgerRepository;
        this.currentUserService = currentUserService;
        this.redisService = redisService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AddTransactionResponse addTransaction(AddTransactionRequest addTransactionRequest) {
        //new Transaction
        AddTransactionResponse addTransactionResponse = null;
        if (addTransactionRequest.getTransactionId() == null){
            //find the user who is paying in the transaction
            User user = userRepository.findById(addTransactionRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            //find the group
            Group group = groupRepository.findById(addTransactionRequest.getGroup()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            if (group.getStatus() == GroupStatus.ACTIVE.getStatus()){
                //find the category
                SubCategory category = categoryRepository.findByName(addTransactionRequest.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                Transaction transaction = modelMapper.map(addTransactionRequest, Transaction.class);
                transaction.setUser(user);
                transaction.setDescription(addTransactionRequest.getDescription().trim());
                transaction.setGroup(group);
                transaction.setCategory(category);
                transaction.setStatus(TransactionStatus.ACTIVE.getStatus()); //set the status of transaction as active
                transaction = transactionRepository.save(transaction);

                //check if transaction is split using PERCENTAGE.
                // If percentage then, calculate the amount for each user and assign to that
                //Also add the condition to check if the sum of percentages must be equal to 100
                if (addTransactionRequest.getSplitBy().equals(SplitBy.PERCENTAGE)){
                    // Get the map containing user IDs and their corresponding percentages
                    // Get the total amount from the request
                    double totalAmount = addTransactionRequest.getAmount();

                    // Create a new map to store the calculated amounts
                    Map<String, Double> calculatedAmounts = new HashMap<>();

                    // Iterate over the map and calculate the amounts
                    for (Map.Entry<String, Double> entry : addTransactionRequest.getUsersInvolved().entrySet()) {
                        String userId = entry.getKey();
                        Double percentage = entry.getValue();

                        // Calculate the amount for this user based on the percentage
                        double calculatedAmount = (percentage / 100) * totalAmount;

                        // Store the calculated amount in the new map
                        calculatedAmounts.put(userId, calculatedAmount);
                    }

                    // Update the original map with the calculated amounts
                    addTransactionRequest.setUsersInvolved(calculatedAmounts);
                }

                //check the total amount set to all the users must be equal to the transaction amount
                Double amount = 0.0;
                for (Map.Entry<String, Double> entry : addTransactionRequest.getUsersInvolved().entrySet()) {
                    amount += entry.getValue();
                }

                //if amount is not equal then throw error
                if (!amount.equals(addTransactionRequest.getAmount())) {
                    throw new BadApiRequestException("The sum of amount distributed should be equal to transaction amount");
                }

                Optional<Map.Entry<String, Double>> result = addTransactionRequest.getUsersInvolved().entrySet().stream()
                        .filter(entry -> entry.getKey().equals(user.getEmail())).findFirst();

                if (result.isEmpty()){
                    throw new BadApiRequestException("Paying user must be included in the involved user list");
                }

                //add the transaction amount in total group spending
                group.setTotalAmount(group.getTotalAmount()+addTransactionRequest.getAmount());
                groupRepository.save(group);

                //Now add the split money to user ledger
                for (Map.Entry<String, Double> entry : addTransactionRequest.getUsersInvolved().entrySet()) {
                    if (entry.getKey().isBlank()) //There was a case where empty email and 0.0 amount was passed, in that case skip
                        continue;
                    String userEmail = entry.getKey();
                    Double userAmount = entry.getValue();
                    UserLedger userLedger = new UserLedger();
                    UserGroupLedger userGroupLedger = new UserGroupLedger();
                    User involvedUser = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new ResourceNotFoundException("User with email " + userEmail + " not found"));

                    if (!involvedUser.getUserUuid().equals(addTransactionRequest.getUserUuid())) {
                        userLedger.setTransaction(transaction);
                        userLedger.setUser(involvedUser);
                        userLedger.setLentFrom(user);
                        userLedger.setIsActive(LedgerStatus.ACTIVE.getStatus());
                        userLedger.setOwedOrLent(LentOwedStatus.OWED.toString());
                        userLedger.setAmount(userAmount);

                        //for each user update the user group ledger also
                        userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                        Double totalOwedAmount = userGroupLedger.getTotalOwed();
                        userGroupLedger.setTotalOwed(totalOwedAmount + userAmount);
                        try {
                            userGroupLedgerRepository.save(userGroupLedger);
                        } catch (StackOverflowError e) {
                            throw e;
                        }
                    }
                    else {
                        userLedger.setTransaction(transaction);
                        userLedger.setUser(involvedUser);
                        userLedger.setLentFrom(null);
                        userLedger.setIsActive(LedgerStatus.ACTIVE.getStatus());
                        userLedger.setOwedOrLent(LentOwedStatus.LENT.toString());
                        userLedger.setAmount(addTransactionRequest.getAmount() - userAmount);

                        //Modify the totalLent amount in user ledger for the payer
                        userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(user, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                        Double totalLentAmount = userGroupLedger.getTotalLent();
                        userGroupLedger.setTotalLent(totalLentAmount + (addTransactionRequest.getAmount() - userAmount));
                        try {
                            userGroupLedgerRepository.save(userGroupLedger);
                        } catch (StackOverflowError e) {
                            throw e;
                        }
                    }
                    try {
                        userLedgerRepository.save(userLedger);
                    } catch (StackOverflowError e) {
                        throw e;
                    }
                }
                // Ensure transaction is committed before calling stored procedure
                entityManager.flush();
                entityManager.clear();
                transactionRepository.calculateNetBalance(addTransactionRequest.getGroup()); //set the net balance from the stored procedure
                transactionRepository.resetEqualBalances(addTransactionRequest.getGroup());
                addTransactionResponse = modelMapper.map(transaction, AddTransactionResponse.class);
            } else {
                throw new BadApiRequestException("Something went wrong");
            }

            return addTransactionResponse;
            }
        else {
            throw new BadApiRequestException("The group is no longer active");
        }
    }

    @Override
    @Transactional
    public AddTransactionResponse updateTransaction(AddTransactionRequest addTransactionRequest) {
        //Update the transaction

        //find the transaction
        Transaction transaction = transactionRepository.findById(addTransactionRequest.getTransactionId()).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        //Verify both transaction and group is in active state
        if (transaction.getStatus() == TransactionStatus.ACTIVE.getStatus() && transaction.getGroup().getStatus() == GroupStatus.ACTIVE.getStatus()){
            // Create a separate list to avoid ConcurrentModificationException
            List<UserLedger> userLedgers = new ArrayList<>(transaction.getUserLedger());

            //Update the user group ledger for each user
            for (UserLedger userLedger: userLedgers){
                User involvedUser = userLedger.getUser();
                Double userAmount = userLedger.getAmount();
                UserGroupLedger userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, transaction.getGroup()).orElseThrow(() -> new ResourceNotFoundException("User group ledger details not found"));

                if (!involvedUser.getUserUuid().equals(transaction.getUser().getUserUuid())){
                    userGroupLedger.setTotalOwed(userGroupLedger.getTotalOwed() - userAmount);
                } else {
                    userGroupLedger.setTotalLent(userGroupLedger.getTotalLent() - userAmount);
                }
                userGroupLedgerRepository.save(userGroupLedger);
                transaction.getUserLedger().remove(userLedger);
            }

            entityManager.flush();
            entityManager.clear();
            transactionRepository.calculateNetBalance(addTransactionRequest.getGroup());
            transactionRepository.resetEqualBalances(addTransactionRequest.getGroup());
            //update the transaction details
            String emptyTransaction = "UPDATE Transaction t SET t.amount = 0.00, t.category = null, t.group = null, t.user = null, t.description = null WHERE t.id = :id";
            entityManager.createQuery(emptyTransaction)
                    .setParameter("id", addTransactionRequest.getTransactionId())
                    .executeUpdate();

            //add new transaction data

            //find the user who is paying in the transaction
            User user = userRepository.findById(addTransactionRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            //find the group
            Group group = groupRepository.findById(addTransactionRequest.getGroup()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

            group.setTotalAmount((group.getTotalAmount() - transaction.getAmount())+addTransactionRequest.getAmount());
            //find the category
            SubCategory category = categoryRepository.findByName(addTransactionRequest.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            transaction = modelMapper.map(addTransactionRequest, Transaction.class);
            transaction.setUser(user);
            transaction.setGroup(group);
            transaction.setCategory(category);
            transaction.setDescription(addTransactionRequest.getDescription().trim());
            transaction = transactionRepository.save(transaction);

            //check the total amount set to all the users must be equal to the transaction amount
            Double amount = 0.0;
            for (Map.Entry<String, Double> entry : addTransactionRequest.getUsersInvolved().entrySet()) {
                amount += entry.getValue();
            }

            //if amount is not equal then throw error
            if (!amount.equals(addTransactionRequest.getAmount())) {
                throw new BadApiRequestException("The sum of amount distributed should be equal to transaction amount");
            }

            Optional<Map.Entry<String, Double>> result = addTransactionRequest.getUsersInvolved().entrySet().stream()
                    .filter(entry -> entry.getKey().equals(user.getEmail())).findFirst();

            if (result.isEmpty()){
                throw new BadApiRequestException("Paying user must be included in the involved user list");
            }

            //Now add the split money to user ledger
            for (Map.Entry<String, Double> entry : addTransactionRequest.getUsersInvolved().entrySet()) {
                String userEmail = entry.getKey();
                Double userAmount = entry.getValue();
                UserLedger userLedger = new UserLedger();
                UserGroupLedger userGroupLedger = new UserGroupLedger();
                User involvedUser = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("User with email " + userEmail + " not found"));

                if (!involvedUser.getUserUuid().equals(addTransactionRequest.getUserUuid())) {
                    userLedger.setTransaction(transaction);
                    userLedger.setUser(involvedUser);
                    userLedger.setLentFrom(user);
                    userLedger.setIsActive(LedgerStatus.ACTIVE.getStatus());
                    userLedger.setOwedOrLent(LentOwedStatus.OWED.toString());
                    userLedger.setAmount(userAmount);

                    //for each user update the user group ledger also
                    userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                    Double totalOwedAmount = userGroupLedger.getTotalOwed();
                    userGroupLedger.setTotalOwed(totalOwedAmount + userAmount);
                    try {
                        userGroupLedgerRepository.save(userGroupLedger);
                    } catch (StackOverflowError e) {
                        throw e;
                    }
                }
                else {
                    userLedger.setTransaction(transaction);
                    userLedger.setUser(involvedUser);
                    userLedger.setLentFrom(null);
                    userLedger.setIsActive(LedgerStatus.ACTIVE.getStatus());
                    userLedger.setOwedOrLent(LentOwedStatus.LENT.toString());
                    userLedger.setAmount(addTransactionRequest.getAmount() - userAmount);

                    //Modify the totalLent amount in user ledger for the payer
                    userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(user, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                    Double totalLentAmount = userGroupLedger.getTotalLent();
                    userGroupLedger.setTotalLent(totalLentAmount + (addTransactionRequest.getAmount() - userAmount));
                    try {
                        userGroupLedgerRepository.save(userGroupLedger);
                    } catch (StackOverflowError e) {
                        throw e;
                    }
                }
                try {
                    userLedgerRepository.save(userLedger);
                } catch (StackOverflowError e) {
                    throw e;
                }
            }
            // Ensure transaction is committed before calling stored procedure
            entityManager.flush();
            entityManager.clear();
            transactionRepository.calculateNetBalance(addTransactionRequest.getGroup()); //set the net balance from the stored procedure
            transactionRepository.resetEqualBalances(addTransactionRequest.getGroup());
            AddTransactionResponse addTransactionResponse = modelMapper.map(transaction, AddTransactionResponse.class);

            return addTransactionResponse;
        }
        else{
            throw new BadApiRequestException("Either transaction or group is no longer in active state");
        }
    }

    @Override
    @Transactional
    public CalculatedDebtResponse calculateDebt(Integer groupId) {

        //Find the Group
        Group group = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group not found"));

        //Create the creditor and debtor map
        List<UserGroupLedger> userGroupLedgerList = userGroupLedgerRepository.findByGroup(group).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Map<String, Double> creditors = new HashMap<>();
        Map<String, Double> debtors = new HashMap<>();

        for (UserGroupLedger userGroupLedger: userGroupLedgerList){
            if (userGroupLedger.getNetBalance() < 0.0){
                // User is a creditor
                creditors.put(userGroupLedger.getUser().getUserUuid(), Math.abs(userGroupLedger.getNetBalance()));
            } else if (userGroupLedger.getNetBalance() > 0.0){
                // User is a debtor
                debtors.put(userGroupLedger.getUser().getUserUuid(), Math.abs(userGroupLedger.getNetBalance())); // Use positive values for debtors for easier processing
            }
        }
        return calculateDebts(creditors, debtors, group);
    }

    @Override
    @Transactional
    public SettleUpTransactionResponse settleUpTransaction(SettleUpTransactionRequest settleUpTransactionRequest) {
        //find the user who is paying in the transaction
        User payer = userRepository.findById(settleUpTransactionRequest.getPayer()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //find the user who is receiving in the transaction
        User receiver = userRepository.findById(settleUpTransactionRequest.getReceiver()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //find the group
        Group group = groupRepository.findById(settleUpTransactionRequest.getGroup()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        //Verify group is in active state
        if (group.getStatus() == GroupStatus.ACTIVE.getStatus()){
            Transaction transaction = modelMapper.map(settleUpTransactionRequest, Transaction.class);
            transaction.setUser(payer);
            transaction.setGroup(group);
            transaction.setCategory(null);
            transaction.setStatus(TransactionStatus.ACTIVE.getStatus()); //set the status of transaction as active
            transaction.setSplitBy(SplitBy.EQUAL);
            transaction = transactionRepository.save(transaction);

            UserLedger userLedgerPayer = new UserLedger();
            UserLedger userLedgerReceiver = new UserLedger();
            UserGroupLedger userGroupLedgerPayer = new UserGroupLedger();
            UserGroupLedger userGroupLedgerReceiver = new UserGroupLedger();

            List<UserLedger> userLedgerList = new ArrayList<>();

            //Set user ledger for the receiver
            userLedgerReceiver.setTransaction(transaction);
            userLedgerReceiver.setUser(receiver);
            userLedgerReceiver.setLentFrom(payer);
            userLedgerReceiver.setIsActive(LedgerStatus.ACTIVE.getStatus());
            userLedgerReceiver.setOwedOrLent(LentOwedStatus.OWED.toString());
            userLedgerReceiver.setAmount(settleUpTransactionRequest.getAmount());

            userLedgerList.add(userLedgerReceiver);

            //Set user ledger for the payer
            userLedgerPayer.setTransaction(transaction);
            userLedgerPayer.setUser(payer);
            userLedgerPayer.setLentFrom(null);
            userLedgerPayer.setIsActive(LedgerStatus.ACTIVE.getStatus());
            userLedgerPayer.setOwedOrLent(LentOwedStatus.LENT.toString());
            userLedgerPayer.setAmount(settleUpTransactionRequest.getAmount());
            userLedgerList.add(userLedgerPayer);

            userLedgerRepository.saveAll(userLedgerList);

            List<UserGroupLedger> userGroupLedgerList = new ArrayList<>();
            //Set user group ledger for the receiver
            userGroupLedgerReceiver = userGroupLedgerRepository.findByUserAndGroup(receiver, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            Double totalOwedAmount = userGroupLedgerReceiver.getTotalOwed();
            userGroupLedgerReceiver.setTotalOwed(totalOwedAmount + settleUpTransactionRequest.getAmount());
            userGroupLedgerList.add(userGroupLedgerReceiver);

            //Set user group ledger for the payer
            userGroupLedgerPayer = userGroupLedgerRepository.findByUserAndGroup(payer, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            Double totalLentAmount = userGroupLedgerPayer.getTotalLent();
            userGroupLedgerPayer.setTotalLent(totalLentAmount + settleUpTransactionRequest.getAmount());
            userGroupLedgerList.add(userGroupLedgerPayer);

            try {
                userGroupLedgerRepository.saveAll(userGroupLedgerList);
            } catch (StackOverflowError e) {
                throw e;
            }

            SettleUpTransactionResponse settleUpTransactionResponse = new SettleUpTransactionResponse();
            settleUpTransactionResponse.setTransactionId(transaction.getTransactionId());
            settleUpTransactionResponse.setAmount(settleUpTransactionRequest.getAmount());
            settleUpTransactionResponse.setPayer(payer.getUserUuid());
            settleUpTransactionResponse.setPayerName(payer.getName());
            settleUpTransactionResponse.setReceiverName(receiver.getName());
            settleUpTransactionResponse.setReceiver(receiver.getUserUuid());
            settleUpTransactionResponse.setGroupId(settleUpTransactionRequest.getGroup());
            settleUpTransactionResponse.setCreatedOn(transaction.getCreatedOn());
            entityManager.flush();
            entityManager.clear();
            transactionRepository.calculateNetBalance(settleUpTransactionRequest.getGroup());
            transactionRepository.resetEqualBalances(settleUpTransactionRequest.getGroup());
            return settleUpTransactionResponse;
        } else {
            throw new BadApiRequestException("This group is no longer in active state");
        }
    }

    @Override
    @Transactional
    public DeleteResponse deleteTransaction(Integer transactionId) {
        //find the transaction
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        //Verify transaction should be in active status
        if (transaction.getStatus() == TransactionStatus.ACTIVE.getStatus()){
            User user = transaction.getUser();
            Group group = transaction.getGroup();

            group.setTotalAmount(group.getTotalAmount()-transaction.getAmount());
            groupRepository.save(group);

            // Create a separate list to avoid ConcurrentModificationException
            List<UserLedger> userLedgers = new ArrayList<>(transaction.getUserLedger());

            //Update the user group ledger for each user
            for (UserLedger userLedger: userLedgers){
                User involvedUser = userLedger.getUser();
                Double userAmount = userLedger.getAmount();
                UserGroupLedger userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, group).orElseThrow(() -> new ResourceNotFoundException("User group ledger details not found"));

                if (!involvedUser.getUserUuid().equals(user.getUserUuid())){
                    userGroupLedger.setTotalOwed(userGroupLedger.getTotalOwed() - userAmount);
                } else {
                    userGroupLedger.setTotalLent(userGroupLedger.getTotalLent() - userAmount);
                }
                userGroupLedgerRepository.save(userGroupLedger);
                UserLedger userLedgerFound = userLedgerRepository.findById(userLedger.getLedgerId()).orElseThrow(() -> new ResourceNotFoundException("User ledger not found"));
                userLedgerFound.setIsActive(LedgerStatus.DELETED.getStatus());  //make the user ledger inactive
//                transaction.getUserLedger().remove(userLedger);
            }
            entityManager.flush();
            entityManager.clear();
            transactionRepository.calculateNetBalance(group.getGroupId());
            transactionRepository.resetEqualBalances(group.getGroupId());
            transaction.setStatus(TransactionStatus.DELETED.getStatus()); // make the transaction as deleted and save
            transactionRepository.save(transaction);
//            transactionRepository.delete(transaction);
            return DeleteResponse.builder().message("Transaction deleted successfully").build();
        } else{
            throw new BadApiRequestException("Something went wrong");
        }
    }

    @Override
    public GetTransactionByIdResponse getTransactionById(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        //Verify transaction should be in active status
//        if (transaction.getStatus() == TransactionStatus.ACTIVE.getStatus()){
            //find the user ledger details according to the transaction
            List<UserLedger> userLedgers = userLedgerRepository.findByTransaction(transaction).orElseThrow(() -> new ResourceNotFoundException("User ledger details not found"));

            GetTransactionByIdResponse getTransactionByIdResponse = modelMapper.map(transaction, GetTransactionByIdResponse.class);
            getTransactionByIdResponse.setPayerName(transaction.getUser().getName());

            List<GetTransactionByIdResponse.UserLedgerDetails> userLedgerDetails = userLedgers.stream().map(userLedger -> GetTransactionByIdResponse.UserLedgerDetails.builder()
                    .amount(userLedger.getAmount())
                    .name(userLedger.getUser().getName())
                    .userUuid(userLedger.getUser().getUserUuid())
                    .owedOrLent(userLedger.getOwedOrLent())
                    .build()).toList();
            getTransactionByIdResponse.setUserLedgerDetails(userLedgerDetails);

            return getTransactionByIdResponse;
//        } else {
//            throw new BadApiRequestException("This transaction is no longer in active state");
//        }
    }

    @Override
    public List<GetTransactionByGroupResponse> getTransactionsByGroupId(Integer groupId) {
//        //Check if data is already present in the redis or no
//        List<GetTransactionByGroupResponse> getTransactionByGroupResponse = redisService.getList("transactions_of_"+groupId, new TypeReference<List<GetTransactionByGroupResponse>>(){});
//        if (getTransactionByGroupResponse != null){
//            return getTransactionByGroupResponse;
//        } else {
            Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            //Verify group is in active state
            if (group.getStatus() == GroupStatus.ACTIVE.getStatus()){
                List<Transaction> transactions = transactionRepository.findByGroupAndStatus(group, TransactionStatus.ACTIVE.getStatus()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                List<GetTransactionByGroupResponse> transactionResponses = new ArrayList<>(transactions.stream().map(transaction -> {
                    LoggedInUserTransaction loggedInUserTransaction = userLedgerRepository.findByTransactionAndUser(transaction, currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("User not found"))).map(userLedger1 -> LoggedInUserTransaction.builder()
                                    .userUuid(userLedger1.getUser().getUserUuid())
                                    .amount(userLedger1.getAmount())
                                    .owedOrLent(userLedger1.getOwedOrLent())
                                    .build())
                            .orElse(null);

                    GetTransactionByGroupResponse getTransactionByGroupResponses = modelMapper.map(transaction, GetTransactionByGroupResponse.class);

                    if (transaction.getDescription() == null && transaction.getCategory() == null) {
                        //This transaction is settle up transaction
                        List<UserLedger> userLedgerList = userLedgerRepository.findByTransaction(transaction).orElseThrow(() -> new ResourceNotFoundException("User ledger details not found"));

                        for (UserLedger userLedger : userLedgerList) {
                            if (userLedger.getOwedOrLent().equalsIgnoreCase("OWED")) {
                                getTransactionByGroupResponses.setReceiver(userLedger.getUser().getUserUuid());
                                getTransactionByGroupResponses.setReceiverName(userLedger.getUser().getName());
                            }
                        }
                    }

                    getTransactionByGroupResponses.setPayerName(transaction.getUser().getName());
                    getTransactionByGroupResponses.setLoggedInUserTransaction(loggedInUserTransaction);
                    return getTransactionByGroupResponses;
                }).toList());
                // Sort transactionResponses by createdOn in descending order (latest date first)
                transactionResponses.sort(Comparator.comparing(GetTransactionByGroupResponse::getCreatedOn).reversed());
                if (transactionResponses != null){
                    redisService.set("transactions_of_"+groupId, transactionResponses, 3000L);
                }
                return transactionResponses;
            } else {
                throw new BadApiRequestException("This group is no longer in active state");
            }
//        }
    }

    @Override
    public List<GetTransactionByGroupResponse> getTransactionsByUser(String userUuid) {
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Transaction> transactions = userLedgerRepository.findTransactionsByUser(user).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        List<GetTransactionByGroupResponse> getTransactionByGroupResponses = transactions.stream().map(transaction -> modelMapper.map(transaction, GetTransactionByGroupResponse.class)).collect(toList());
        return getTransactionByGroupResponses;
    }

    @Override
    @Transactional
    public GetTransactionByIdResponse restoreTransactionById(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        //verify if group is still active. if active then only restore otherwise show alert
        if (transaction.getGroup().getStatus() == GroupStatus.ACTIVE.getStatus()){
            if (transaction.getStatus() == TransactionStatus.DELETED.getStatus()){
                User user = transaction.getUser();
                Group group = transaction.getGroup();

                group.setTotalAmount(group.getTotalAmount()+transaction.getAmount());
                groupRepository.save(group);

                // Create a separate list to avoid ConcurrentModificationException
                List<UserLedger> userLedgers = new ArrayList<>(transaction.getUserLedger());

                //Update the user group ledger for each user
                for (UserLedger userLedger: userLedgers){
                    User involvedUser = userLedger.getUser();
                    Double userAmount = userLedger.getAmount();
                    UserGroupLedger userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, group).orElseThrow(() -> new ResourceNotFoundException("User group ledger details not found"));

                    if (!involvedUser.getUserUuid().equals(user.getUserUuid())){
                        userGroupLedger.setTotalOwed(userGroupLedger.getTotalOwed() + userAmount);
                    } else {
                        userGroupLedger.setTotalLent(userGroupLedger.getTotalLent() + userAmount);
                    }
                    userGroupLedgerRepository.save(userGroupLedger);
                    userLedger.setIsActive(LedgerStatus.ACTIVE.getStatus());  //make the user ledger active
                }
                entityManager.flush();
                entityManager.clear();
                transactionRepository.calculateNetBalance(group.getGroupId());
                transactionRepository.resetEqualBalances(group.getGroupId());
                transaction.setStatus(TransactionStatus.ACTIVE.getStatus()); // make the transaction as active and save
                transactionRepository.save(transaction);
                return getTransactionById(transactionId);
            } else {
                throw new BadApiRequestException("Transaction is already restored");
            }
        } else {
            throw new BadApiRequestException("Transaction cannot be restored as the parent group does not exists more");
        }
    }

    @Transactional
    public CalculatedDebtResponse calculateDebts(Map<String, Double> creditorsMap, Map<String, Double> debtorsMap, Group group) {
        // Sort debtors map by keys (user names) in ascending order
        TreeMap<String, Double> sortedDebtorsMap = new TreeMap<>(debtorsMap);
        List<CalculatedDebtResponse.Creditor> creditorList = new ArrayList<>();
        List<CalculatedDebtResponse.Debtor> debtorList = new ArrayList<>();

        // Create a map to store the lending details for each creditor
        Map<String, List<CalculatedDebtResponse.LentDetails>> creditorLendingMap = new HashMap<>();
        Map<String, List<CalculatedDebtResponse.LentDetails>> debtorLendingMap = new HashMap<>();

        // Process transactions
        for (Map.Entry<String, Double> debtorEntry : sortedDebtorsMap.entrySet()) {
            User debtorUser = userRepository.findById(debtorEntry.getKey()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            double debtorAmount = debtorEntry.getValue();

            for (Map.Entry<String, Double> creditorEntry : creditorsMap.entrySet()) {
                User creditorUser = userRepository.findById(creditorEntry.getKey()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                double creditorAmount = creditorEntry.getValue();

                if (debtorAmount > 0 && creditorAmount > 0) {
                    double settlementAmount = Math.min(debtorAmount, creditorAmount);

                    // Add lending details for creditor
                    creditorLendingMap.computeIfAbsent(creditorUser.getUserUuid(), k -> new ArrayList<>())
                            .add(CalculatedDebtResponse.LentDetails.builder()
                                    .uuid(debtorUser.getUserUuid())
                                    .name(debtorUser.getName())
                                    .amount(settlementAmount)
                                    .build());

                    // Add lending details for debtor
                    debtorLendingMap.computeIfAbsent(debtorUser.getUserUuid(), k -> new ArrayList<>())
                            .add(CalculatedDebtResponse.LentDetails.builder()
                                    .uuid(creditorUser.getUserUuid())
                                    .name(creditorUser.getName())
                                    .amount(settlementAmount)
                                    .build());

                    System.out.println(debtorUser.getName() + " pays " + settlementAmount + " to " + creditorUser.getName());

                    // Update debtor's amount
                    debtorAmount -= settlementAmount;
                    debtorsMap.put(debtorUser.getUserUuid(), debtorAmount);

                    // Update creditor's amount
                    creditorAmount -= settlementAmount;
                    creditorsMap.put(creditorUser.getUserUuid(), creditorAmount);
                }
            }
        }
        // Create Creditor list for response
        for (Map.Entry<String, List<CalculatedDebtResponse.LentDetails>> entry : creditorLendingMap.entrySet()) {
            double getsBackAmount = entry.getValue().stream().mapToDouble(CalculatedDebtResponse.LentDetails::getAmount).sum();
            User creditorUser = userRepository.findById(entry.getKey()).orElseThrow(() -> new ResourceNotFoundException("Creditor user not found"));
            creditorList.add(CalculatedDebtResponse.Creditor.builder()
                    .uuid(creditorUser.getUserUuid())
                    .name(creditorUser.getName())
                    .getsBack(getsBackAmount)
                    .lentTo(entry.getValue())
                    .build());
        }

        // Create Debtor list for response
        for (Map.Entry<String, List<CalculatedDebtResponse.LentDetails>> entry : debtorLendingMap.entrySet()) {
            double totalReturnAmount = entry.getValue().stream().mapToDouble(CalculatedDebtResponse.LentDetails::getAmount).sum();
            User debtorUser = userRepository.findById(entry.getKey()).orElseThrow(() -> new ResourceNotFoundException("Creditor user not found"));
            debtorList.add(CalculatedDebtResponse.Debtor.builder()
                    .uuid(debtorUser.getUserUuid())
                    .totalReturnAmount(totalReturnAmount)
                    .name(debtorUser.getName())
                    .lentFrom(entry.getValue())
                    .build());
        }

        CalculatedDebtResponse calculatedDebtResponse = CalculatedDebtResponse.builder()
                .creditorList(creditorList)
                .debtorList(debtorList)
                .build();

        // Print JSON representation
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(calculatedDebtResponse);
            log.info("Calculated Debt Response JSON:");
            log.info(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return calculatedDebtResponse;
    }

}
