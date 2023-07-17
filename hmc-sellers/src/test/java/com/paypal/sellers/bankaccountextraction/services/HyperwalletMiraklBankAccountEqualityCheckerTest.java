package com.paypal.sellers.bankaccountextraction.services;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@formatter:off
class HyperwalletMiraklBankAccountEqualityCheckerTest {

	private final HyperwalletMiraklBankAccountEqualityChecker testObj = new HyperwalletMiraklBankAccountEqualityChecker();

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsIBAN(final HyperwalletBankAccount hyperwalletBankAccount, final IBANBankAccountModel miraklBankAccount, final boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsABA(final HyperwalletBankAccount hyperwalletBankAccount, final ABABankAccountModel miraklBankAccount, final boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsCanadian(final HyperwalletBankAccount hyperwalletBankAccount, final CanadianBankAccountModel miraklBankAccount, final boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsUK(final HyperwalletBankAccount hyperwalletBankAccount, final UKBankAccountModel miraklBankAccount, final boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@Test
	void isSameBankAccount_ShouldThrowException_WhenBankAccountTypeIsNotSupported() {
		final BankAccountModel miraklBankAccount = new BankAccountModel(BankAccountModel.builder()
				.transferMethodCurrency("USD")
				.transferMethodCountry("US")
				.bankAccountNumber("BAN1")) {};
		final HyperwalletBankAccount hyperwalletBankAccount = createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1");
		assertThatThrownBy(() ->testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private static Stream<? extends Arguments> isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsIBAN() {
		return Stream.of(
				Arguments.of(
						createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createIBANBankAccountModel("USD", "US", "BI1", "BAN1"),
						true),
				Arguments.of(
						createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createIBANBankAccountModel("EUR", "US", "BI1", "BAN1"),
						false),
				Arguments.of(createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createIBANBankAccountModel("USD", "CA", "BI1", "BAN1"),
						false),
				Arguments.of(createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createIBANBankAccountModel("USD", "US", "BI2", "BAN1"),
						false),
				Arguments.of(createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createIBANBankAccountModel("USD", "US", "BI1", "BA2"),
						false));
	}

	private static Stream<? extends Arguments> isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsABA() {
		return Stream.of(
				Arguments.of(
						createABAHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createABABankAccountModel("USD", "US", "BI1", "BAN1"),
						true),
				Arguments.of(
						createABAHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createABABankAccountModel("EUR", "US", "BI1", "BAN1"),
						false),
				Arguments.of(createABAHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createABABankAccountModel("USD", "CA", "BI1", "BAN1"),
						false),
				Arguments.of(createABAHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createABABankAccountModel("USD", "US", "BI2", "BAN1"),
						false),
				Arguments.of(createABAHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createABABankAccountModel("USD", "US", "BI1", "BA2"),
						false));
	}

	private static Stream<? extends Arguments> isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsCanadian() {
		return Stream.of(
				Arguments.of(
						createCanadianHyperwalletBankAccount("USD", "US", "BRI1", "BI1", "BAN1"),
						createCanadianBankAccountModel("USD", "US", "BRI1", "BI1", "BAN1"),
						true),
				Arguments.of(
						createCanadianHyperwalletBankAccount("USD", "US", "BRI1", "BI1", "BAN1"),
						createCanadianBankAccountModel("USD", "US", "BRI2", "BI1", "BAN1"),
						false),
				Arguments.of(
						createCanadianHyperwalletBankAccount("USD", "US", "BRI1", "BI1", "BAN1"),
						createCanadianBankAccountModel("EUR", "US", "BRI1", "BI1", "BAN1"),
						false),
				Arguments.of(createCanadianHyperwalletBankAccount("USD", "US", "BRI1", "BI1", "BAN1"),
						createCanadianBankAccountModel("USD", "CA", "BRI1", "BI1", "BAN1"),
						false),
				Arguments.of(createCanadianHyperwalletBankAccount("USD", "US", "BRI1", "BI1", "BAN1"),
						createCanadianBankAccountModel("USD", "US", "BRI1", "BI2", "BAN1"),
						false),
				Arguments.of(createCanadianHyperwalletBankAccount("USD", "US", "BRI1", "BI1", "BAN1"),
						createCanadianBankAccountModel("USD", "US", "BRI1", "BI1", "BA2"),
						false));
	}

	private static Stream<? extends Arguments> isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsUK() {
		return Stream.of(
				Arguments.of(
						createUKHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createUKBankAccountModel("USD", "US", "BI1", "BAN1"),
						true),
				Arguments.of(
						createUKHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createUKBankAccountModel("EUR", "US", "BI1", "BAN1"),
						false),
				Arguments.of(createUKHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createUKBankAccountModel("USD", "CA", "BI1", "BAN1"),
						false),
				Arguments.of(createUKHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createUKBankAccountModel("USD", "US", "BI2", "BAN1"),
						false),
				Arguments.of(createUKHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
						createUKBankAccountModel("USD", "US", "BI1", "BA2"),
						false));
	}

	private static HyperwalletBankAccount createIbanHyperwalletBankAccount(final String currency, final String country, final String bankId,
																		   final String bankAccountId) {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBankId(bankId);
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static HyperwalletBankAccount createABAHyperwalletBankAccount(final String currency, final String country, final String branchId,
																		  final String bankAccountId) {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBranchId(branchId);
		hyperwalletBankAccount.setBankAccountPurpose("CHECKING");
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static HyperwalletBankAccount createCanadianHyperwalletBankAccount(final String currency, final String country, final String branchId,
																			   final String bankId, final String bankAccountId) {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBankId(bankId);
		hyperwalletBankAccount.setBranchId(branchId);
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static HyperwalletBankAccount createUKHyperwalletBankAccount(final String currency, final String country, final String bankId,
																		 final String bankAccountId) {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBankId(bankId);
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static IBANBankAccountModel createIBANBankAccountModel(final String currency, final String country, final String bankId,
																   final String bankAccountId) {
		return IBANBankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.bankBic(bankId)
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

	private static ABABankAccountModel createABABankAccountModel(final String currency, final String country, final String bankId,
																 final String bankAccountId) {
		return ABABankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.branchId(bankId)
				.bankAccountPurpose("CHECKING")
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

	private static CanadianBankAccountModel createCanadianBankAccountModel(final String currency, final String country, final String branchId,
																		   final String bankId, final String bankAccountId) {
		return CanadianBankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.bankId(bankId)
				.branchId(branchId)
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

	private static UKBankAccountModel createUKBankAccountModel(final String currency, final String country, final String bankId,
															   final String bankAccountId) {
		return UKBankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.bankAccountId(bankId)
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

}
