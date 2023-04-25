package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

//@formatter:off
@Component
public class HyperwalletMiraklBankAccountMatcher {

	private final HyperwalletMiraklBankAccountCompatibilityChecker hyperwalletMiraklBankAccountCompatibilityChecker;

	private final HyperwalletMiraklBankAccountEqualityChecker hyperwalletMiraklBankAccountEqualityChecker;

	public HyperwalletMiraklBankAccountMatcher(HyperwalletMiraklBankAccountCompatibilityChecker hyperwalletMiraklBankAccountCompatibilityChecker, HyperwalletMiraklBankAccountEqualityChecker hyperwalletMiraklBankAccountEqualityChecker) {
		this.hyperwalletMiraklBankAccountCompatibilityChecker = hyperwalletMiraklBankAccountCompatibilityChecker;
		this.hyperwalletMiraklBankAccountEqualityChecker = hyperwalletMiraklBankAccountEqualityChecker;
	}

	public Optional<HyperwalletBankAccount> findExactOrCompatibleMatch(List<HyperwalletBankAccount> hyperwalletCandidates, BankAccountModel miraklBankAccount) {
		return findSameTokenCompatibleBankAccount(hyperwalletCandidates, miraklBankAccount)
				.or(() -> findExactBankAccount(hyperwalletCandidates, miraklBankAccount))
				.or(() -> findCompatibleBankAccount(hyperwalletCandidates, miraklBankAccount));
	}

	private Optional<HyperwalletBankAccount> findExactBankAccount(List<HyperwalletBankAccount> hyperwalletCandidates, BankAccountModel miraklBankAccount) {
		return hyperwalletCandidates.stream()
				.filter(candidate -> hyperwalletMiraklBankAccountEqualityChecker.isSameBankAccount(candidate, miraklBankAccount))
				.findFirst();
	}

	private Optional<HyperwalletBankAccount> findCompatibleBankAccount(List<HyperwalletBankAccount> hyperwalletCandidates, BankAccountModel miraklBankAccount) {
		return hyperwalletCandidates.stream()
				.filter(candidate -> hyperwalletMiraklBankAccountCompatibilityChecker.isBankAccountCompatible(candidate, miraklBankAccount))
				.findFirst();
	}

	private Optional<HyperwalletBankAccount> findSameTokenCompatibleBankAccount(List<HyperwalletBankAccount> hyperwalletCandidates, BankAccountModel miraklBankAccount) {
		return hyperwalletCandidates.stream()
				.filter(candidate -> candidate.getToken().equals(miraklBankAccount.getToken()))
				.filter(candidate -> hyperwalletMiraklBankAccountCompatibilityChecker.isBankAccountCompatible(candidate, miraklBankAccount))
				.findFirst();
	}

}
