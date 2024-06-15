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

import java.util.Map;
import java.util.Objects;

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
    @Transactional(rollbackFor = Exception.class)
    public AddTransactionResponse addTransaction(AddTransactionRequest addTransactionRequest) {
        try {
            //find the user who is paying in the transaction
            User user = userRepository.findById(addTransactionRequest.getUserUuid()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            //find the group
            Group group = groupRepository.findById(addTransactionRequest.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            //find the category
            Category category = categoryRepository.findByName(addTransactionRequest.getCategory()).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));

            Transaction transaction = modelMapper.map(addTransactionRequest, Transaction.class);
            transaction.setUser(user);
            transaction.setGroup(group);
            transaction.setCategory(category);
            transactionRepository.save(transaction);

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
                    userLedger = new UserLedger();
                    userLedger.setTransaction(transaction);
                    userLedger.setUser(involvedUser);
                    userLedger.setLentFrom(user);
                    userLedger.setIsActive(LedgerStatus.ACTIVE);
                    userLedger.setOwedOrLent(LentOwedStatus.OWED.toString());
                    userLedger.setAmount(userAmount);

                    //for each user update the user group ledger also
                    userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(involvedUser, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                    Double totalOwedAmount = userGroupLedger.getTotalOwed();
                    userGroupLedger.setTotalLent(totalOwedAmount + userAmount);
                    log.info("Creating user group ledger");
                    userGroupLedgerRepository.save(userGroupLedger);
                    log.info("Created user group ledger");
                } else {
                    userLedger = new UserLedger();
                    userLedger.setTransaction(transaction);
                    userLedger.setUser(involvedUser);
                    userLedger.setLentFrom(null);
                    userLedger.setIsActive(LedgerStatus.ACTIVE);
                    userLedger.setOwedOrLent(LentOwedStatus.LENT.toString());
                    userLedger.setAmount(addTransactionRequest.getAmount() - userAmount);

                    //Modify the totalLent amount in user ledger for the payer
                    userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(user, group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
                    Double totalLentAmount = userGroupLedger.getTotalLent();
                    userGroupLedger.setTotalLent(totalLentAmount + (addTransactionRequest.getAmount() - userAmount));
                    userGroupLedgerRepository.save(userGroupLedger);
                }
                userLedgerRepository.save(userLedger);
            }

            AddTransactionResponse addTransactionResponse = modelMapper.map(transaction, AddTransactionResponse.class);
            return addTransactionResponse;
        } catch (Exception e){
            throw new BadApiRequestException("Something went wrong");
        }
    }
}
