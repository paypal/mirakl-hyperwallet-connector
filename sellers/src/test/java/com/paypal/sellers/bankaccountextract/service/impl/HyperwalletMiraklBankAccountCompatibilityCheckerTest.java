package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.converter.impl.miraklshop.bankaccounttype.HyperwalletBankAccountTypeResolver;
import com.paypal.sellers.bankaccountextract.model.BankAccountType;
import com.paypal.sellers.bankaccountextract.model.IBANBankAccountModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletMiraklBankAccountCompatibilityCheckerTest {

	@InjectMocks
	private HyperwalletMiraklBankAccountCompatibilityChecker testObj;

	@Mock
	private HyperwalletBankAccountTypeResolver hyperwalletBankAccountTypeResolverMock;

	@Test
	void isBankAccountCompatible_shouldReturnTrue_whenSameBankAccountTypeAndSameBankAccountCountry() {
		IBANBankAccountModel miraklBankAccount = IBANBankAccountModel.builder().transferMethodCountry("US")
				.type(BankAccountType.ABA).build();
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCountry("US");

		when(hyperwalletBankAccountTypeResolverMock.getBankAccountType(hyperwalletBankAccount))
				.thenReturn(BankAccountType.ABA);

		assertThat(testObj.isBankAccountCompatible(hyperwalletBankAccount, miraklBankAccount)).isTrue();
	}

	@Test
	void isBankAccountCompatible_shouldReturnFalse_whenDifferentBankAccountTypeAndSameBankAccountCountry() {
		IBANBankAccountModel miraklBankAccount = IBANBankAccountModel.builder().transferMethodCountry("US")
				.type(BankAccountType.IBAN).build();
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCountry("US");

		when(hyperwalletBankAccountTypeResolverMock.getBankAccountType(hyperwalletBankAccount))
				.thenReturn(BankAccountType.ABA);

		assertThat(testObj.isBankAccountCompatible(hyperwalletBankAccount, miraklBankAccount)).isFalse();
	}

	@Test
	void isBankAccountCompatible_shouldReturnFalse_whenSameBankAccountTypeAndDifferentBankAccountCountry() {
		IBANBankAccountModel miraklBankAccount = IBANBankAccountModel.builder().transferMethodCountry("ES")
				.type(BankAccountType.ABA).build();
		HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setTransferMethodCountry("US");

		when(hyperwalletBankAccountTypeResolverMock.getBankAccountType(hyperwalletBankAccount))
				.thenReturn(BankAccountType.ABA);

		assertThat(testObj.isBankAccountCompatible(hyperwalletBankAccount, miraklBankAccount)).isFalse();
	}

}
