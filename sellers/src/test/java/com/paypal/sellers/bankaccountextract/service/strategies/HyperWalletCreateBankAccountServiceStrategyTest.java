package com.paypal.sellers.bankaccountextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.bankaccountextract.service.MiraklBankAccountExtractService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletCreateBankAccountServiceStrategyTest {

	private static final String TOKEN = "token";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Spy
	@InjectMocks
	private HyperWalletCreateBankAccountServiceStrategyBankAccount testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private HyperwalletBankAccount hyperwalletBankAccountRequestMock, hyperwalletBankAccountResultMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private MiraklBankAccountExtractService miraklBankAccountExtractServiceMock;

	@Mock
	private BankAccountModel bankAccountModelMock;

	@Test
	void execute_shouldCallSuperExecuteAndUpdateBankAccountToken() {
		doReturn(Optional.of(hyperwalletBankAccountRequestMock)).when(testObj).callSuperExecute(sellerModelMock);

		final Optional<HyperwalletBankAccount> result = testObj.execute(sellerModelMock);

		verify(testObj).callSuperExecute(sellerModelMock);
		verify(miraklBankAccountExtractServiceMock).updateBankAccountToken(sellerModelMock,
				hyperwalletBankAccountRequestMock);
		assertThat(result).isPresent().contains(hyperwalletBankAccountRequestMock);
	}

	@Test
	void callHyperwalletAPI_shouldCreateBankAccount() {
		when(hyperwalletMock.createBankAccount(hyperwalletBankAccountRequestMock))
				.thenReturn(hyperwalletBankAccountResultMock);

		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletMock);

		final HyperwalletBankAccount result = testObj.callHyperwalletAPI(HYPERWALLET_PROGRAM,
				hyperwalletBankAccountRequestMock);

		verify(hyperwalletSDKServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		verify(hyperwalletMock).createBankAccount(hyperwalletBankAccountRequestMock);
		assertThat(result).isEqualTo(hyperwalletBankAccountResultMock);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenBankAccountReceivedAsParameterHasANullToken() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(bankAccountModelMock);
		when(bankAccountModelMock.getToken()).thenReturn(null);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenBankAccountReceivedAsParameterIsNotEmpty() {
		when(sellerModelMock.getBankAccountDetails()).thenReturn(bankAccountModelMock);
		when(bankAccountModelMock.getToken()).thenReturn(TOKEN);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

}
