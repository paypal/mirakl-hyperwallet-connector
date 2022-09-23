package com.paypal.sellers.bankaccountextract.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.mirakl.client.core.exception.MiraklApiException;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.sellers.bankaccountextract.model.BankAccountModel;
import com.paypal.sellers.bankaccountextract.service.MiraklBankAccountExtractService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountTokenSynchronizationServiceImplTest {

	private static final String BANK_ACCOUNT_TOKEN_VALUE = "bankToken";

	private static final String SELLER_TOKEN_VALUE = "sellerToken";

	private static final String PROGRAM_TOKEN = "programToken";

	@InjectMocks
	private BankAccountTokenSynchronizationServiceImpl testObj;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private MiraklBankAccountExtractService miraklBankAccountExtractServiceMock;

	@Mock
	private Hyperwallet hyperwalletSDKMock;

	@Test
	void synchronizeToken_ShouldReturnCurrentSellerModel_WhenSellerBankAccountDetailsAreNull() {

		final SellerModel originalSellerModel = SellerModel.builder().build();

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result).isEqualTo(originalSellerModel);
	}

	@Test
	void synchronizeToken_ShouldReturnCurrentSellerModel_WhenSellerBankAccountDetailsTokenIsNotNull() {

		final SellerModel originalSellerModel = SellerModel.builder()
				.bankAccountDetails(BankAccountModel.builder().token(BANK_ACCOUNT_TOKEN_VALUE).build()).build();

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result).isEqualTo(originalSellerModel);
	}

	@Test
	void synchronizeToken_ShouldReturnCurrentSellerModel_WhenSellerBankAccountDetailsTokenIsBlankAndBankAccountDoesNotExistInHW() {

		final SellerModel originalSellerModel = SellerModel.builder()
				.bankAccountDetails(BankAccountModel.builder().token(BANK_ACCOUNT_TOKEN_VALUE).build()).build();

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result).isEqualTo(originalSellerModel);
	}

	@Test
	void synchronizeToken_ShouldThrowHMCHyperwalletAPIException_WhenHWRequestThrowAHyperwalletException() {

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN).bankAccountDetails(BankAccountModel.builder().build()).build();

		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE))
				.thenThrow(new HyperwalletException("Something went wrong"));

		AssertionsForClassTypes.assertThatThrownBy(() -> testObj.synchronizeToken(originalSellerModel))
				.isInstanceOf(HMCHyperwalletAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Hyperwallet API");
	}

	@Test
	void synchronizeToken_ShouldThrowHMCMiraklAPIException_WhenMiraklRequestThrowAMiraklApiException() {

		final HyperwalletList<HyperwalletBankAccount> bankAccountHyperwalletList = new HyperwalletList<>();
		final HyperwalletBankAccount bankAccount = new HyperwalletBankAccount();
		bankAccount.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		bankAccountHyperwalletList.setData(List.of(bankAccount));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(bankAccountHyperwalletList);

		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN).bankAccountDetails(BankAccountModel.builder().build()).build();

		doThrow(MiraklApiException.class).when(miraklBankAccountExtractServiceMock)
				.updateBankAccountToken(originalSellerModel, bankAccount);

		AssertionsForClassTypes.assertThatThrownBy(() -> testObj.synchronizeToken(originalSellerModel))
				.isInstanceOf(HMCMiraklAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Mirakl API");
	}

	@Test
	void synchronizeToken_ShouldReturnASynchronizedBankAccount_WhenSellerBankAccountDetailsTokenIsBlankAndBankAccountDoesExistInHW() {

		final HyperwalletList<HyperwalletBankAccount> bankAccountHyperwalletList = new HyperwalletList<>();
		final HyperwalletBankAccount bankAccount = new HyperwalletBankAccount();
		bankAccount.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		bankAccountHyperwalletList.setData(List.of(bankAccount));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(bankAccountHyperwalletList);

		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN).bankAccountDetails(BankAccountModel.builder().build()).build();

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		verify(miraklBankAccountExtractServiceMock).updateBankAccountToken(originalSellerModel, bankAccount);
		assertThat(result.getBankAccountDetails().getToken()).isEqualTo(BANK_ACCOUNT_TOKEN_VALUE);
		assertThat(result.getToken()).isEqualTo(SELLER_TOKEN_VALUE);
		assertThat(result.getProgramToken()).isEqualTo(PROGRAM_TOKEN);
	}

}
