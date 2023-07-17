package com.paypal.sellers.bankaccountextraction.services.converters.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.CanadianBankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.IBANBankAccountModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerModelToHyperWalletCanadianBankAccountTest {

	private static final String BRANCH_ID = "branchId";

	private static final String BANK_ID = "bankId";

	@Spy
	@InjectMocks
	private SellerModelToHyperWalletCanadianBankAccount testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private CanadianBankAccountModel canadianBankAccountModelMock;

	@Mock
	private IBANBankAccountModel ibanBankAccountModelMock;

	@Test
	void execute_shouldReturnCanadianHyperwalletBankAccount() {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();

		when(sellerModelMock.getBankAccountDetails()).thenReturn(canadianBankAccountModelMock);
		when(canadianBankAccountModelMock.getBankId()).thenReturn(BANK_ID);
		when(canadianBankAccountModelMock.getBranchId()).thenReturn(BRANCH_ID);
		doReturn(hyperwalletBankAccount).when(testObj).callSuperConvert(sellerModelMock);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result.getBranchId()).isEqualTo(BRANCH_ID);
		assertThat(result.getBankId()).isEqualTo(BANK_ID);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBankAccountDetailsIsCanadianBankAccountType() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(canadianBankAccountModelMock);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenBankAccountDetailsIsDifferentFromCanadianBankAccountType() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(ibanBankAccountModelMock);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenNullPaymentInformationIsReceived() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(null);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

}
