package com.vr.SplitEase.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_ledger")
public class UserLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Integer ledgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @JoinColumn(name = "transaction")
    @ManyToOne(fetch = FetchType.LAZY)
    private Transaction transaction;
    @Column(name = "owed_or_lent", nullable = false)
    private String owedOrLent;
    @Column(name = "lent_from")
    private User lentFrom;
    private Double amount;
    @Column(name = "is_active")
    private Integer isActive;

}
