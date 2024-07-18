package com.vr.SplitEase.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculatedDebtResponse {
    List<Creditor> creditorList;
    List<Debtor> debtorList;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Creditor{
        private String uuid;
        private String name;
        private Double getsBack;
        private List<LentDetails> lentTo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Debtor{
        private String uuid;
        private String name;
        private List<LentDetails> lentFrom;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LentDetails{
        private String uuid;
        private String name;
        private Double amount;
    }
}
