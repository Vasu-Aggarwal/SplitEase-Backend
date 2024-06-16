package com.vr.SplitEase.util;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
public class DebtSettlement {

    public void settleDebts(Map<String, Double> creditorsMap, Map<String, Double> debtorsMap) {
        // Sort debtors map by keys (user names) in ascending order
        TreeMap<String, Double> sortedDebtorsMap = new TreeMap<>(debtorsMap);

        // Process transactions
        for (Map.Entry<String, Double> debtorEntry : sortedDebtorsMap.entrySet()) {
            String debtor = debtorEntry.getKey();
            double debtorAmount = debtorEntry.getValue();

            for (Map.Entry<String, Double> creditorEntry : creditorsMap.entrySet()) {
                String creditor = creditorEntry.getKey();
                double creditorAmount = creditorEntry.getValue();

                if (debtorAmount > 0 && creditorAmount > 0) {
                    double settlementAmount = Math.min(debtorAmount, creditorAmount);

                    // Perform transaction from debtor to creditor
                    System.out.println(debtor + " pays " + settlementAmount + " to " + creditor);

                    // Update debtor's amount
                    debtorAmount -= settlementAmount;
                    debtorsMap.put(debtor, debtorAmount);

                    // Update creditor's amount
                    creditorAmount -= settlementAmount;
                    creditorsMap.put(creditor, creditorAmount);
                }
            }
        }
    }
}