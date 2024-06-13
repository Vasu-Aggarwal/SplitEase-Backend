package com.vr.SplitEase.entity;

import com.vr.SplitEase.config.constants.UserGroupStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "user_group_ledger")
public class UserGroupLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userGroupLedgerId;

    @ManyToOne
    private User user;

    @ManyToOne
    private Group group;

    private Double totalOwed;
    private Double totalLent;
    private UserGroupStatus status;

}
