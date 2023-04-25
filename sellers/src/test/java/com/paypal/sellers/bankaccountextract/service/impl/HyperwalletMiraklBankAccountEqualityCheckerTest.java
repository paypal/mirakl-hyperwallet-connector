package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@formatter:off
class HyperwalletMiraklBankAccountEqualityCheckerTest {

	private HyperwalletMiraklBankAccountEqualityChecker testObj = new HyperwalletMiraklBankAccountEqualityChecker();

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsIBAN(HyperwalletBankAccount hyperwalletBankAccount, IBANBankAccountModel miraklBankAccount, boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsABA(HyperwalletBankAccount hyperwalletBankAccount, ABABankAccountModel miraklBankAccount, boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsCanadian(HyperwalletBankAccount hyperwalletBankAccount, CanadianBankAccountModel miraklBankAccount, boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@ParameterizedTest
	@MethodSource
	void isSameBankAccount_shouldDetectSameBankAccount_whenBankAccountTypeIsUK(HyperwalletBankAccount hyperwalletBankAccount, UKBankAccountModel miraklBankAccount, boolean areEquals) {
		assertThat(testObj.isSameBankAccount(hyperwalletBankAccount, miraklBankAccount)).isEqualTo(areEquals);
	}

	@Test
	void isSameBankAccount_ShouldThrowException_WhenBankAccountTypeIsNotSupported() {
		BankAccountModel miraklBankAccount = new BankAccountModel(BankAccountModel.builder()
				.transferMethodCurrency("USD")
				.transferMethodCountry("US")
				.bankAccountNumber("BAN1")) {};
		assertThatThrownBy(() ->testObj.isSameBankAccount(
				createIbanHyperwalletBankAccount("USD", "US", "BI1", "BAN1"),
				miraklBankAccount)).isInstanceOf(IllegalArgumentException.class);

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

	private static HyperwalletBankAccount createIbanHyperwalletBankAccount(String currency, String country, String bankId,
			String bankAccountId) {
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBankId(bankId);
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static HyperwalletBankAccount createABAHyperwalletBankAccount(String currency, String country, String branchId,
																		   String bankAccountId) {
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBranchId(branchId);
		hyperwalletBankAccount.setBankAccountPurpose("CHECKING");
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static HyperwalletBankAccount createCanadianHyperwalletBankAccount(String currency, String country, String branchId,
																			   String bankId, String bankAccountId) {
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBankId(bankId);
		hyperwalletBankAccount.setBranchId(branchId);
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static HyperwalletBankAccount createUKHyperwalletBankAccount(String currency, String country, String bankId,
																		 String bankAccountId) {
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCurrency(currency);
		hyperwalletBankAccount.setTransferMethodCountry(country);
		hyperwalletBankAccount.setBankId(bankId);
		hyperwalletBankAccount.setBankAccountId("*******" + bankAccountId);
		return hyperwalletBankAccount;
	}

	private static IBANBankAccountModel createIBANBankAccountModel(String currency, String country, String bankId,
			String bankAccountId) {
		return IBANBankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.bankBic(bankId)
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

	private static ABABankAccountModel createABABankAccountModel(String currency, String country, String bankId,
																   String bankAccountId) {
		return ABABankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.branchId(bankId)
				.bankAccountPurpose("CHECKING")
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

	private static CanadianBankAccountModel createCanadianBankAccountModel(String currency, String country, String branchId,
																		   String bankId, String bankAccountId) {
		return CanadianBankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.bankId(bankId)
				.branchId(branchId)
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

	private static UKBankAccountModel createUKBankAccountModel(String currency, String country, String bankId,
			String bankAccountId) {
		return UKBankAccountModel.builder()
				.transferMethodCountry(country)
				.transferMethodCurrency(currency)
				.bankAccountId(bankId)
				.bankAccountNumber("1234567890" + bankAccountId)
				.build();
	}

}
