package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.config.constants.LedgerStatus;
import com.vr.SplitEase.config.constants.LentOwedStatus;
import com.vr.SplitEase.dto.request.AddTransactionRequest;
import com.vr.SplitEase.dto.response.AddTransactionResponse;
import com.vr.SplitEase.entity.*;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.*;
import com.vr.SplitEase.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CategoryRepository categoryRepository;
    private final UserGroupLedgerRepository userGroupLedgerRepository;
    private final UserLedgerRepository userLedgerRepository;

    public TransactionServiceImpl(ModelMapper modelMapper, TransactionRepository transactionRepository, UserRepository userRepository, GroupRepository groupRepository, CategoryRepository categoryRepository, UserGroupLedgerRepository userGroupLedgerRepository, UserLedgerRepository userLedgerRepository) {
        this.modelMapper = modelMapper;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
        this.userGroupLedgerRepository = userGroupLedgerRepository;
        this.userLedgerRepository = userLedgerRepository;
    }

    @Override
    @Transactional
    public AddTransactionResponse addTransaction(AddTransactionRequest addTransactionRequest) {
            //find the user who is paying in the transaction
            User user = userRepository.findById(addTransactionRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            //find the group
            Group group = groupRepository.findById(addTransactionRequest.getGroup()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            //find the category
            Category category = categoryRepository.findByName(addTransactionRequest.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            Transaction transaction = modelMapper.map(addTransactionRequest, Transaction.class);
            transaction.setUser(user);
            transaction.setGroup(group);
            transaction.setCategory(category);
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
                    userLedger.setIsActive(LedgerStatus.ACTIVE);
                    userLedger.setOwedOrLent(LentOwedStatus.OWED.toString());
                    userLedger.setAmount(userAmount);
//                    log.info("Fetching user group ledger for user: {} and group: {}", involvedUser.getUserUuid(), group.getGroupId());

                    //for each user update the user group ledger also
                    userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                    Double totalOwedAmount = userGroupLedger.getTotalOwed();
                    Double netBalance = userGroupLedger.getNetBalance();
//                    log.info("Updating total lent amount from {} to {}", totalOwedAmount, totalOwedAmount+userAmount);
                    userGroupLedger.setTotalOwed(totalOwedAmount + userAmount);
                    userGroupLedger.setNetBalance(netBalance + totalOwedAmount);
//                    log.info("Creating user group ledger");
                    try {
                        userGroupLedgerRepository.save(userGroupLedger);
//                        log.info("User group ledger updated successfully");
                    } catch (StackOverflowError e) {
//                        log.error("StackOverflowError while saving user group ledger for user: {} and group: {}. Error: {}", involvedUser.getUserUuid(), group.getGroupId(), e.getMessage());
                        throw e;
                    }
//                    log.info("Created user group ledger");
                }
                else {
                    userLedger.setTransaction(transaction);
                    userLedger.setUser(involvedUser);
                    userLedger.setLentFrom(null);
                    userLedger.setIsActive(LedgerStatus.ACTIVE);
                    userLedger.setOwedOrLent(LentOwedStatus.LENT.toString());
                    userLedger.setAmount(addTransactionRequest.getAmount() - userAmount);

                    //Modify the totalLent amount in user ledger for the payer
                    userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(user, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                    Double totalLentAmount = userGroupLedger.getTotalLent();
                    Double netBalance = userGroupLedger.getNetBalance();
                    userGroupLedger.setTotalLent(totalLentAmount + (addTransactionRequest.getAmount() - userAmount));
                    userGroupLedger.setNetBalance(netBalance - (totalLentAmount + (addTransactionRequest.getAmount() - userAmount)));
                    try {
                        userGroupLedgerRepository.save(userGroupLedger);
//                        log.info("User group ledger updated successfully");
                    } catch (StackOverflowError e) {
//                        log.error("StackOverflowError while saving user group ledger for user: {} and group: {}. Error: {}", involvedUser.getUserUuid(), group.getGroupId(), e.getMessage());
                        throw e;
                    }
                }
//                log.info("Creating user ledger");
                try {
                    userLedgerRepository.save(userLedger);
//                    log.info("User  ledger updated successfully");
                } catch (StackOverflowError e) {
//                    log.error("StackOverflowError while saving user  ledger for user: {} and group: {}. Error: {}", involvedUser.getUserUuid(), group.getGroupId(), e.getMessage());
                    throw e;
                }
//                log.info("Created user ledger");
            }

            AddTransactionResponse addTransactionResponse = modelMapper.map(transaction, AddTransactionResponse.class);

            Group group1 = groupRepository.findById(1).orElseThrow(() -> new ResourceNotFoundException("group not found"));
            calculateDebt(group1);
            return addTransactionResponse;
    }

    @Override
    @Transactional
    public void calculateDebt(Group group) {
        //Create the creditor and debtor map
        List<UserGroupLedger> userGroupLedgerList = userGroupLedgerRepository.findByGroup(group).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Map<String, Double> creditors = new HashMap<>();
        Map<String, Double> debtors = new HashMap<>();

        for (UserGroupLedger userGroupLedger: userGroupLedgerList){
            if (userGroupLedger.getNetBalance() > 0.0){
                // User is a creditor
                creditors.put(userGroupLedger.getUser().getUserUuid(), userGroupLedger.getNetBalance());
            } else if (userGroupLedger.getNetBalance() < 0.0){
                // User is a debtor
                debtors.put(userGroupLedger.getUser().getUserUuid(), Math.abs(userGroupLedger.getNetBalance())); // Use positive values for debtors for easier processing
            }
        }
        calculateDebts(creditors, debtors, group);
    }

    @Transactional
    public void calculateDebts(Map<String, Double> creditorsMap, Map<String, Double> debtorsMap, Group group) {
        // Sort debtors map by keys (user names) in ascending order
        TreeMap<String, Double> sortedDebtorsMap = new TreeMap<>(debtorsMap);

        // Process transactions
        for (Map.Entry<String, Double> debtorEntry : sortedDebtorsMap.entrySet()) {
//            String debtor = debtorEntry.getKey();
            User debtorUser = userRepository.findById(debtorEntry.getKey()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
            double debtorAmount = debtorEntry.getValue();

            for (Map.Entry<String, Double> creditorEntry : creditorsMap.entrySet()) {
                User creditorUser = userRepository.findById(creditorEntry.getKey()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                double creditorAmount = creditorEntry.getValue();

                if (debtorAmount > 0 && creditorAmount > 0) {
                    double settlementAmount = Math.min(debtorAmount, creditorAmount);

                    // Perform transaction from debtor to creditor
                    UserGroupLedger userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(debtorUser, group).orElseThrow(() -> new ResourceNotFoundException("User Group Ledger does not exists"));
                    userGroupLedger.setLentFrom(creditorUser);
                    userGroupLedger.setNetBalance(settlementAmount);
                    userGroupLedger.setTotalLent(0.0);
                    userGroupLedger.setTotalOwed(0.0);
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
    }
}
