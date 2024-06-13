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
    private Integer ledgerId;

    private User user;
    private Transaction transaction;
    private String owedOrLent;
    private User lentFrom;
    private Double amount;
    private Integer isActive;

}
