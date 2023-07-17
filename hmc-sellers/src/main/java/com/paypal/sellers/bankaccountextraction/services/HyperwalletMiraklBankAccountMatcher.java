package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.BankAccountModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

//@formatter:off
@Component
public class HyperwalletMiraklBankAccountMatcher {

	private final HyperwalletMiraklBankAccountCompatibilityChecker hyperwalletMiraklBankAccountCompatibilityChecker;

	private final HyperwalletMiraklBankAccountEqualityChecker hyperwalletMiraklBankAccountEqualityChecker;

	public HyperwalletMiraklBankAccountMatcher(final HyperwalletMiraklBankAccountCompatibilityChecker hyperwalletMiraklBankAccountCompatibilityChecker, final HyperwalletMiraklBankAccountEqualityChecker hyperwalletMiraklBankAccountEqualityChecker) {
		this.hyperwalletMiraklBankAccountCompatibilityChecker = hyperwalletMiraklBankAccountCompatibilityChecker;
		this.hyperwalletMiraklBankAccountEqualityChecker = hyperwalletMiraklBankAccountEqualityChecker;
	}

	public Optional<HyperwalletBankAccount> findExactOrCompatibleMatch(final List<HyperwalletBankAccount> hyperwalletCandidates, final BankAccountModel miraklBankAccount) {
		return findSameTokenCompatibleBankAccount(hyperwalletCandidates, miraklBankAccount)
				.or(() -> findExactBankAccount(hyperwalletCandidates, miraklBankAccount))
				.or(() -> findCompatibleBankAccount(hyperwalletCandidates, miraklBankAccount));
	}

	private Optional<HyperwalletBankAccount> findExactBankAccount(final List<HyperwalletBankAccount> hyperwalletCandidates, final BankAccountModel miraklBankAccount) {
		return hyperwalletCandidates.stream()
				.filter(candidate -> hyperwalletMiraklBankAccountEqualityChecker.isSameBankAccount(candidate, miraklBankAccount))
				.findFirst();
	}

	private Optional<HyperwalletBankAccount> findCompatibleBankAccount(final List<HyperwalletBankAccount> hyperwalletCandidates, final BankAccountModel miraklBankAccount) {
		return hyperwalletCandidates.stream()
				.filter(candidate -> hyperwalletMiraklBankAccountCompatibilityChecker.isBankAccountCompatible(candidate, miraklBankAccount))
				.findFirst();
	}

	private Optional<HyperwalletBankAccount> findSameTokenCompatibleBankAccount(final List<HyperwalletBankAccount> hyperwalletCandidates, final BankAccountModel miraklBankAccount) {
		return hyperwalletCandidates.stream()
				.filter(candidate -> candidate.getToken().equals(miraklBankAccount.getToken()))
				.filter(candidate -> hyperwalletMiraklBankAccountCompatibilityChecker.isBankAccountCompatible(candidate, miraklBankAccount))
				.findFirst();
	}

}
