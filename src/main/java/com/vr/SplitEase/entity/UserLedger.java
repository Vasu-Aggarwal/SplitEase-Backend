package com.vr.SplitEase.entity;

import com.vr.SplitEase.config.constants.LedgerStatus;
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
public class UserLedger extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Integer ledgerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid")
    private User user;

    @JoinColumn(name = "transaction_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Transaction transaction;

    @Column(name = "owed_or_lent", nullable = false)
    private String owedOrLent;

    private Double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lent_from")
    private User lentFrom;

    @Column(name = "is_active")
    private LedgerStatus isActive;

}
