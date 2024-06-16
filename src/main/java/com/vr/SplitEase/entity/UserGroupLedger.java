package com.vr.SplitEase.entity;

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
@Table(name = "user_group_ledger", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_uuid", "group_id"})})
public class UserGroupLedger{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_group_ledger_id")
    private Integer userGroupLedgerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid")
    private User user;

    @JoinColumn(name = "group_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Group group;

    @Column(name = "total_owed")
    private Double totalOwed;
    @Column(name = "total_lent")
    private Double totalLent;
    private Integer status;

}
