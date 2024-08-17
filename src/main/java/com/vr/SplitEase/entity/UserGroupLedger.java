package com.vr.SplitEase.entity;

import com.vr.SplitEase.config.constants.GroupStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "user_group_ledger", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_uuid", "group_id"})})
public class UserGroupLedger{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_group_ledger_id")
    private Integer userGroupLedgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lentFrom")
    private User lentFrom;

    @Column(name = "net_balance")
    private Double netBalance;

    @JoinColumn(name = "group_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @Column(name = "total_owed")
    private Double totalOwed;
    @Column(name = "total_lent")
    private Double totalLent;
    @Enumerated(EnumType.STRING)
    private GroupStatus status;
}
