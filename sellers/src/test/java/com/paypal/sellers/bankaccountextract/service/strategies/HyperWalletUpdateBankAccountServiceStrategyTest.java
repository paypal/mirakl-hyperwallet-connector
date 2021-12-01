package com.paypal.sellers.bankaccountextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletUpdateBankAccountServiceStrategyTest {

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@InjectMocks
	private HyperWalletUpdateBankAccountServiceStrategyBankAccount testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private HyperwalletBankAccount hyperwalletBankAccountRequestMock, hyperwalletBankAccountResultMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private BankAccountModel bankAccountModelMock;

	private static final String TOKEN = "token";

	@Test
	void callHyperwalletAPI_shouldCreateBankAccount() {
		when(hyperwalletMock.updateBankAccount(hyperwalletBankAccountRequestMock))
				.thenReturn(hyperwalletBankAccountResultMock);
		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletMock);

		final HyperwalletBankAccount result = testObj.callHyperwalletAPI(HYPERWALLET_PROGRAM,
				hyperwalletBankAccountRequestMock);

		verify(hyperwalletSDKServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		verify(hyperwalletMock).updateBankAccount(hyperwalletBankAccountRequestMock);
		assertThat(result).isEqualTo(hyperwalletBankAccountResultMock);
	}

	@Test
	void isApplicable_shouldReturnFalseWhenBankAccountReceivedAsParameterHasANullToken() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(bankAccountModelMock);
		when(bankAccountModelMock.getToken()).thenReturn(null);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBankAccountReceivedAsParameterIsNotEmpty() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(bankAccountModelMock);
		when(bankAccountModelMock.getToken()).thenReturn(TOKEN);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

}
