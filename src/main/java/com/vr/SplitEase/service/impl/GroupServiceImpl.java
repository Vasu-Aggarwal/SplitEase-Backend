package com.vr.SplitEase.service.impl;

import com.cloudinary.Cloudinary;
import com.vr.SplitEase.config.EmailScheduler;
import com.vr.SplitEase.config.constants.AppConstants;
import com.vr.SplitEase.config.constants.GroupStatus;
import com.vr.SplitEase.config.constants.LentOwedStatus;
import com.vr.SplitEase.dto.request.AddGroupRequest;
import com.vr.SplitEase.dto.request.AddUserToGroupRequest;
import com.vr.SplitEase.dto.response.*;
import com.vr.SplitEase.entity.*;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.CannotRemoveUserFromGroupException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.*;
import com.vr.SplitEase.service.CloudinaryImageService;
import com.vr.SplitEase.service.EmailService;
import com.vr.SplitEase.service.GroupService;
import jakarta.mail.MessagingException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;
    private final UserGroupLedgerRepository userGroupLedgerRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserLedgerRepository userLedgerRepository;
    private final EmailService emailService;
    private final EmailScheduler emailScheduler;
    private final CloudinaryImageService cloudinaryImageService;

    public GroupServiceImpl(ModelMapper modelMapper, GroupRepository groupRepository, UserGroupLedgerRepository userGroupLedgerRepository, CurrentUserService currentUserService, UserRepository userRepository, TransactionRepository transactionRepository, UserLedgerRepository userLedgerRepository, EmailService emailService, EmailScheduler emailScheduler, CloudinaryImageService cloudinaryImageService) {
        this.modelMapper = modelMapper;
        this.groupRepository = groupRepository;
        this.userGroupLedgerRepository = userGroupLedgerRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.userLedgerRepository = userLedgerRepository;
        this.emailService = emailService;
        this.emailScheduler = emailScheduler;
        this.cloudinaryImageService = cloudinaryImageService;
    }


    @Override
    @Transactional
    public AddGroupResponse addUpdateGroup(String name, Integer groupId, MultipartFile image) {
        Group group = new Group();
        if (groupId != null){
            group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
            if (image != null && !image.isEmpty()) {
                Map imageResponse = cloudinaryImageService.uploadImage(image);
                group.setImageUrl(imageResponse.get("url").toString());
            }
            //Allow update of active state groups.
            if (Objects.equals(group.getStatus(), GroupStatus.ACTIVE.getStatus())){
                group.setName(name);
            } else {
                throw new BadApiRequestException("Cannot update inactive group.");
            }
            groupRepository.save(group);
        } else {
            group.setName(name);
            group.setStatus(GroupStatus.ACTIVE.getStatus());
            group.setTotalAmount(0.0);
            group.setUser(currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong")));
            if (image != null && !image.isEmpty()) {
                Map imageResponse = cloudinaryImageService.uploadImage(image);
                group.setImageUrl(imageResponse.get("url").toString());
            }
            groupRepository.save(group);
            //Add the user (who is creating the group) to the group
            UserGroupLedger userGroupLedger = new UserGroupLedger();
            userGroupLedger.setUser(currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Something went wrong")));
            userGroupLedger.setGroup(group);
            userGroupLedger.setStatus(GroupStatus.ACTIVE.getStatus());
            userGroupLedger.setTotalOwed(0.00);
            userGroupLedger.setTotalLent(0.00);
            userGroupLedger.setNetBalance(0.00);
            userGroupLedgerRepository.save(userGroupLedger);
        }
        return modelMapper.map(group, AddGroupResponse.class);
    }

    @Override
    @Transactional
    public AddUserToGroupResponse addUsersToGroup(AddUserToGroupRequest addUserToGroupRequest) throws MessagingException {
        User sender = currentUserService.getCurrentUser().orElseThrow(() -> new ResourceNotFoundException("Sender user not found"));

        Set<User> users = addUserToGroupRequest.getUserList().stream().map(userEmail ->
                userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException(String.format("User with email: %s not found", userEmail))))
                .collect(Collectors.toSet());
        Group group = groupRepository.findById(addUserToGroupRequest.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        //check whether no user present in the list is already present with same group
        List<String> conflictingUsers = new ArrayList<>();
        for (User user : users) {
            if (userGroupLedgerRepository.existsByUserAndGroup(user, group)) {
                conflictingUsers.add(user.getEmail());
            }
        }
        if (!conflictingUsers.isEmpty()) {
            throw new BadApiRequestException("Users already part of the group: " + String.join(", ", conflictingUsers));
        }

        try {
            for (User user : users) {
                UserGroupLedger userGroupLedger = new UserGroupLedger();
                userGroupLedger.setUser(user);
                userGroupLedger.setGroup(group);
                userGroupLedger.setStatus(GroupStatus.ACTIVE.getStatus());
                userGroupLedger.setTotalLent(0.00);
                userGroupLedger.setTotalOwed(0.00);
                userGroupLedger.setNetBalance(0.00);
                userGroupLedgerRepository.save(userGroupLedger);
                //send the email to the user
                Map<String, Object> template = new HashMap<>();
                template.put("recipientName", user.getName());
                template.put("senderName", sender.getName());
                template.put("senderEmail", sender.getEmail());
                template.put("groupName", group.getName());
                emailScheduler.scheduleEmail(user.getEmail(), sender.getName() + " added you to the group '"+group.getName()+"' on Splitease", template);
            }
        } catch (Exception e){
            throw new BadApiRequestException(e.getMessage());
        }
        AddUserToGroupResponse addUserToGroupResponse = new AddUserToGroupResponse("Users added successfully");
        return addUserToGroupResponse;
    }

    @Override
    public ByteArrayInputStream generateExcelForGroupTransactions(Integer groupId) throws IOException {
        //Find the group
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        //Find all transactions of a particular group
        List<Transaction> transactions = transactionRepository.findByGroup(group).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet("Transactions");

            // Create a cell style for date format
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));


            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date", "Description", "Category", "Amount"};
            int cellIdx = 0;
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(cellIdx++);
                cell.setCellValue(headers[i]);
            }

            // Add user names to header row dynamically
            List<User> users = new ArrayList<>();
            for (Transaction transaction : transactions) {
                List<UserLedger> userLedgerList = userLedgerRepository.findByTransaction(transaction).orElseThrow(() -> new ResourceNotFoundException("User ledger details not found"));
                for (UserLedger userLedger : userLedgerList) {
                    User user = userLedger.getUser();
                    if (!users.contains(user)) {
                        users.add(user);
                    }
                }
            }

            for (User user : users) {
                Cell cell = headerRow.createCell(cellIdx++);
                cell.setCellValue(user.getName());
            }

            // Populate data rows
            int rowIdx = 1;
            for (Transaction transaction : transactions) {
                Row row = sheet.createRow(rowIdx++);
                Cell dateCell = row.createCell(0);
                dateCell.setCellValue(transaction.getCreatedOn());
                dateCell.setCellStyle(dateCellStyle);
                row.createCell(1).setCellValue(transaction.getDescription());
                row.createCell(2).setCellValue(transaction.getCategory().getName());
                row.createCell(3).setCellValue(transaction.getAmount());

                // Populate user amounts in the row
                cellIdx = 4; // Starting cell for user amounts
                for (User user : users) {
                    double amount = userLedgerRepository.findByTransactionAndUser(transaction, user)
                            .map(userLedger -> {
                                double ledgerAmount = userLedger.getAmount();
                                if (userLedger.getOwedOrLent().equals(LentOwedStatus.LENT.toString())) {
                                    ledgerAmount = -ledgerAmount; // Prefix with '-' if user has lent money
                                }
                                return ledgerAmount;
                            })
                            .orElse(0.0);
                    row.createCell(cellIdx++).setCellValue(amount);
                }
            }

            // Add total net balance row at the end
            Row totalNetBalanceRow = sheet.createRow(++rowIdx);
            totalNetBalanceRow.createCell(0).setCellValue("Total Balance");

            // Fetch and set net balance for each user involved in the transaction
            List<UserGroupLedger> userGroupLedgers = userGroupLedgerRepository.findByGroup(group)
                    .orElseThrow(() -> new ResourceNotFoundException("User group ledger details not found"));


            cellIdx = 4;
            for (User user : users) {
                double netBalance = userGroupLedgers.stream()
                        .filter(ledger -> ledger.getUser().equals(user))
                        .mapToDouble(UserGroupLedger::getNetBalance)
                        .sum();

                Cell cell = totalNetBalanceRow.createCell(cellIdx++);
                cell.setCellValue(netBalance);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new BadApiRequestException(e.getMessage());
        } finally {
            workbook.close();
            out.close();
        }
    }

    @Override
    public DeleteResponse removeUserFromGroup(Integer groupId, String userUuid) {

        //Get the user
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //Get the group
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        //check if user exists in the group or not
        UserGroupLedger userGroupLedger = userGroupLedgerRepository.findByUserAndGroup(user, group).orElseThrow(() -> new ResourceNotFoundException("No user found in the group"));

        //remove the user only if his net balance is 0
        if (userGroupLedger.getNetBalance() == 0.0){
            userGroupLedgerRepository.delete(userGroupLedger);
        } else {
            //When the net balance is not 0 then don't delete the user from the group
            throw new CannotRemoveUserFromGroupException("Net balance must be 0", 0, userGroupLedger.getNetBalance());
        }

        return DeleteResponse.builder().message("User removed from the group").build();
    }

    @Override
    public List<CreateUserResponse> getGroupMembers(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        List<CreateUserResponse> userList = group.getUserGroups().stream().map(userGroupLedger ->
                modelMapper.map(userGroupLedger.getUser(), CreateUserResponse.class)
                ).collect(Collectors.toList());
        return userList;
    }

    @Override
    public List<GetGroupMembersV2Response> getGroupMembersV2(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        List<GetGroupMembersV2Response> userList = group.getUserGroups().stream().map(userGroupLedger ->
                modelMapper.map(userGroupLedger.getUser(), GetGroupMembersV2Response.class)
        ).sorted(Comparator.comparing(GetGroupMembersV2Response::getName))
                .collect(Collectors.toList());
        return userList;
    }

    @Override
    public List<GetGroupsByUserResponse> getGroupsByUserUuid(String userUuid, String searchBy) {
        //get the user from the uuid
        User user = userRepository.findById(userUuid).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        //Get the list of groups the user is part of
        List<UserGroupLedger> userGroupLedgers = userGroupLedgerRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Something went wrong"));
        List<GetGroupsByUserResponse> groups = new ArrayList<>();
        if (searchBy.equalsIgnoreCase(AppConstants.ALL_GROUPS.getValue())){
            groups = userGroupLedgers.stream().map(userGroupLedger -> {
                GetGroupsByUserResponse getGroupsByUserResponse = modelMapper.map(userGroupLedger.getGroup(), GetGroupsByUserResponse.class);
                getGroupsByUserResponse.setUserBalance(userGroupLedger.getNetBalance());
                return getGroupsByUserResponse;
            }).sorted(Comparator.comparing(GetGroupsByUserResponse::getName)).collect(Collectors.toList());
        } else if(searchBy.equalsIgnoreCase(AppConstants.GROUPS_YOU_OWE.getValue())){
            groups = userGroupLedgers.stream().filter(userGroupLedger -> userGroupLedger.getNetBalance()>0).map(userGroupLedger -> {
                GetGroupsByUserResponse getGroupsByUserResponse = modelMapper.map(userGroupLedger.getGroup(), GetGroupsByUserResponse.class);
                getGroupsByUserResponse.setUserBalance(userGroupLedger.getNetBalance());
                return getGroupsByUserResponse;
            }).sorted(Comparator.comparing(GetGroupsByUserResponse::getName)).collect(Collectors.toList());
        } else if(searchBy.equalsIgnoreCase(AppConstants.GROUPS_THAT_OWE_YOU.getValue())){
            groups = userGroupLedgers.stream().filter(userGroupLedger -> userGroupLedger.getNetBalance()<0).map(userGroupLedger -> {
                GetGroupsByUserResponse getGroupsByUserResponse = modelMapper.map(userGroupLedger.getGroup(), GetGroupsByUserResponse.class);
                getGroupsByUserResponse.setUserBalance(userGroupLedger.getNetBalance());
                return getGroupsByUserResponse;
            }).sorted(Comparator.comparing(GetGroupsByUserResponse::getName)).collect(Collectors.toList());
        } else if(searchBy.equalsIgnoreCase(AppConstants.OUTSTANDING_BALANCE.getValue())){
            groups = userGroupLedgers.stream().filter(userGroupLedger -> userGroupLedger.getNetBalance()!=0).map(userGroupLedger -> {
                GetGroupsByUserResponse getGroupsByUserResponse = modelMapper.map(userGroupLedger.getGroup(), GetGroupsByUserResponse.class);
                getGroupsByUserResponse.setUserBalance(userGroupLedger.getNetBalance());
                return getGroupsByUserResponse;
            }).sorted(Comparator.comparing(GetGroupsByUserResponse::getName)).collect(Collectors.toList());
        }
        return groups;
    }

    @Override
    public GroupSummaryResponse getGroupSpendingSummary(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group not found"));

        return GroupSummaryResponse.builder()
                .groupId(groupId)
                .groupName(group.getName())
                .totalGroupSpending(group.getTotalAmount())
                .userPaidFor(0.00)
                .userTotalShare(0.00)
                .build();
    }

    @Override
    public AddGroupResponse getGroupInfo(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        AddGroupResponse addGroupResponse = modelMapper.map(group, AddGroupResponse.class);
        return addGroupResponse;
    }
}
