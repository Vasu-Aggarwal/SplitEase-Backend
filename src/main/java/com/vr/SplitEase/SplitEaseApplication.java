package com.vr.SplitEase;

import com.vr.SplitEase.util.DebtSettlement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SplitEaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SplitEaseApplication.class, args);

		DebtSettlement debtSettlement = new DebtSettlement();
		// Example usage with separate maps for creditors and debtors
//		Map<String, Double> creditorsMap = new HashMap<>();
//		creditorsMap.put("User3", 600.0); // Creditor
//		creditorsMap.put("User1", 1200.0); // Creditor
//
//		Map<String, Double> debtorsMap = new HashMap<>();
//		debtorsMap.put("User2", 300.0); // Debtor
////		debtorsMap.put("User3", 300.0); // Debtor
//		debtorsMap.put("User4", 650.0); // Debtor
//		debtorsMap.put("User5", 850.0); // Debtor
		Map<String, Double> creditorsMap = new HashMap<>();
		creditorsMap.put("User3", 3200.0); // Creditor
//		creditorsMap.put("User1", 1200.0); // Creditor

		Map<String, Double> debtorsMap = new HashMap<>();
//		debtorsMap.put("User2", 300.0); // Debtor
//		debtorsMap.put("User3", 300.0); // Debtor
		debtorsMap.put("User4", 1600.0); // Debtor
		debtorsMap.put("User5", 1600.0); // Debtor

		debtSettlement.settleDebts(creditorsMap, debtorsMap);
	}

}
