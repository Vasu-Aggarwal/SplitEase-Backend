package com.vr.SplitEase.entity;

import com.vr.SplitEase.config.constants.GroupStatus;
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
public class UserGroupLedger extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_group_ledger_id")
    private Integer userGroupLedgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    private User user;

    @JoinColumn(name = "group_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @Column(name = "total_owed")
    private Double totalOwed;
    @Column(name = "total_lent")
    private Double totalLent;
    @Enumerated(EnumType.ORDINAL)
    private GroupStatus status;

}