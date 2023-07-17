package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.IBANBankAccountModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletMiraklBankAccountMatcherTest {

	@InjectMocks
	private HyperwalletMiraklBankAccountMatcher testObj;

	@Mock
	private HyperwalletMiraklBankAccountCompatibilityChecker hyperwalletMiraklBankAccountCompatibilityCheckerMock;

	@Mock
	private HyperwalletMiraklBankAccountEqualityChecker hyperwalletMiraklBankAccountEqualityCheckerMock;

	@Test
	void findExactOrCompatibleMatch_shouldReturnBankAccountWithSameToken_whenBankAccountWithSameTokenExists_andIsTheSameBankAccount() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel(1);

		when(hyperwalletMiraklBankAccountEqualityCheckerMock.isSameBankAccount(hyperwalletBankAccounts.get(0),
				miraklBankAccount)).thenReturn(true);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isPresent().contains(hyperwalletBankAccounts.get(0));
	}

	@Test
	void findExactOrCompatibleMatch_shouldReturnBankAccountWithSameToken_whenBankAccountWithSameTokenExists_andIsACompatibleBankAccount() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel(2);

		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(1), miraklBankAccount)).thenReturn(true);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isPresent().contains(hyperwalletBankAccounts.get(1));
	}

	@Test
	void findExactOrCompatibleMatch_shouldReturnBankAccountWithExactMatch_whenBankAccountWithExactMatchExists() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel();

		when(hyperwalletMiraklBankAccountEqualityCheckerMock.isSameBankAccount(hyperwalletBankAccounts.get(0),
				miraklBankAccount)).thenReturn(false);
		when(hyperwalletMiraklBankAccountEqualityCheckerMock.isSameBankAccount(hyperwalletBankAccounts.get(1),
				miraklBankAccount)).thenReturn(true);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isPresent().contains(hyperwalletBankAccounts.get(1));
	}

	@Test
	void findExactOrCompatibleMatch_shouldReturnBankAccountWithOtherToken_whenBankAccountWithSameTokenIsNotCompatible_andIsCompatibleWithOtherBankAccount() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel(1);

		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(0), miraklBankAccount)).thenReturn(false);
		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(1), miraklBankAccount)).thenReturn(true);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isPresent().contains(hyperwalletBankAccounts.get(1));
	}

	@Test
	void findExactOrCompatibleMatch_shouldReturnCompatibleBankAccount_whenBankAccountDoesNotHaveToken_andThereIsACompatibleBankAccount() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel();

		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(0), miraklBankAccount)).thenReturn(false);
		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(1), miraklBankAccount)).thenReturn(true);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isPresent().contains(hyperwalletBankAccounts.get(1));
	}

	@Test
	void findExactOrCompatibleMatch_shouldReturnEmptyOptional_whenBankAccountWithSameTokenDoesNotExist_andIsNotACompatibleBankAccount() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel(3);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isEmpty();
	}

	@Test
	void findExactOrCompatibleMatch_shouldReturnEmptyOptional_whenThereIsNotACompatibleBankAccount_andThereIsNotSameBankAccount() {
		final List<HyperwalletBankAccount> hyperwalletBankAccounts = List.of(hyperwalletBankAccount(1),
				hyperwalletBankAccount(2));

		final IBANBankAccountModel miraklBankAccount = ibanBankAccountModel();

		when(hyperwalletMiraklBankAccountEqualityCheckerMock.isSameBankAccount(hyperwalletBankAccounts.get(0),
				miraklBankAccount)).thenReturn(false);
		when(hyperwalletMiraklBankAccountEqualityCheckerMock.isSameBankAccount(hyperwalletBankAccounts.get(1),
				miraklBankAccount)).thenReturn(false);
		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(0), miraklBankAccount)).thenReturn(false);
		when(hyperwalletMiraklBankAccountCompatibilityCheckerMock
				.isBankAccountCompatible(hyperwalletBankAccounts.get(1), miraklBankAccount)).thenReturn(false);

		final Optional<HyperwalletBankAccount> result = testObj.findExactOrCompatibleMatch(hyperwalletBankAccounts,
				miraklBankAccount);

		assertThat(result).isEmpty();
	}

	private HyperwalletBankAccount hyperwalletBankAccount(final int idx) {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setToken("token" + idx);

		return hyperwalletBankAccount;
	}

	private IBANBankAccountModel ibanBankAccountModel(final int idx) {
		final IBANBankAccountModel ibanBankAccountModel = IBANBankAccountModel.builder().token("token" + idx).build();

		return ibanBankAccountModel;
	}

	private IBANBankAccountModel ibanBankAccountModel() {
		final IBANBankAccountModel ibanBankAccountModel = IBANBankAccountModel.builder().build();

		return ibanBankAccountModel;
	}

}
