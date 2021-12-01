package com.paypal.sellers.bankaccountextract.converter.impl.sellermodel;

import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.ABABankAccountModel;
import com.paypal.sellers.bankaccountextract.model.IBANBankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
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
class SellerModelToHyperWalletIBANBankAccountConverterTest {

	private static final String BANK_BIC = "BankBIC";

	@Spy
	@InjectMocks
	private SellerModelToHyperWalletIBANBankAccount testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private IBANBankAccountModel ibanBankAccountModelMock;

	@Mock
	private ABABankAccountModel abaBankAccountModelMock;

	@Test
	void convert_shouldReturnIBANHyperwalletBankAccount() {
		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();

		when(sellerModelMock.getBankAccountDetails()).thenReturn(ibanBankAccountModelMock);
		when(ibanBankAccountModelMock.getBankBic()).thenReturn(BANK_BIC);
		doReturn(hyperwalletBankAccount).when(testObj).callSuperConvert(sellerModelMock);

		final HyperwalletBankAccount result = testObj.execute(sellerModelMock);

		assertThat(result.getBankId()).isEqualTo(BANK_BIC);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBankAccountDetailsIsIbanBankAccountType() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(ibanBankAccountModelMock);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBankAccountDetailsIsDifferentFromIbanBankAccountType() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(abaBankAccountModelMock);

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
