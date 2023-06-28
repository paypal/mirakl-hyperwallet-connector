package com.paypal.sellers.bankaccountextraction.services.converters.hyperwallet;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextraction.model.ABABankAccountModel;
import com.paypal.sellers.bankaccountextraction.model.BankAccountPurposeType;
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
class SellerModelToHyperWalletABABankAccountConverterTest {

	private static final String BRANCH_ID = "branchId";

	private static final String NY_STATE = "NY";

	private static final String POSTAL_CODE = "NY2000";

	@Spy
	@InjectMocks
	private SellerModelToHyperWalletABABankAccount testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private ABABankAccountModel abaBankAccountModelMock;

	@Mock
	private IBANBankAccountModel ibanBankAccountModelMock;

	@Test
	void execute_shouldReturnABAHyperwalletBankAccount() {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();

		when(sellerModelMock.getBankAccountDetails()).thenReturn(abaBankAccountModelMock);
		when(abaBankAccountModelMock.getBankAccountPurpose()).thenReturn(BankAccountPurposeType.CHECKING.name());
		when(abaBankAccountModelMock.getBranchId()).thenReturn(BRANCH_ID);
		when(abaBankAccountModelMock.getStateProvince()).thenReturn(NY_STATE);
		when(abaBankAccountModelMock.getPostalCode()).thenReturn(POSTAL_CODE);
		doReturn(hyperwalletBankAccount).when(testObj).callSuperConvert(sellerModelMock);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result.getBranchId()).isEqualTo(BRANCH_ID);
		assertThat(result.getBankAccountPurpose()).isEqualTo(BankAccountPurposeType.CHECKING.name());
		assertThat(result.getPostalCode()).isEqualTo(POSTAL_CODE);
		assertThat(result.getStateProvince()).isEqualTo(NY_STATE);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBankAccountDetailsIsAbaBankAccountType() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(abaBankAccountModelMock);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenBankAccountDetailsIsDifferentFromAbaBankAccountType() {
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
