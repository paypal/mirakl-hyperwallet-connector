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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountTokenSynchronizationServiceImplTest {

	private static final String BANK_ACCOUNT_TOKEN_VALUE = "bankToken";

	private static final String BANK_ACCOUNT_TOKEN_VALUE_2 = "bankToken2";

	private static final String BANK_ACCOUNT_NUMBER = "bankAccountNumber";

	private static final String SELLER_TOKEN_VALUE = "sellerToken";

	private static final String PROGRAM_TOKEN = "programToken";

	@InjectMocks
	private BankAccountTokenSynchronizationServiceImpl testObj;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private MiraklBankAccountExtractService miraklBankAccountExtractServiceMock;

	@Mock
	private HyperwalletMiraklBankAccountMatcher miraklBankAccountMatcherMock;

	@Mock
	private Hyperwallet hyperwalletSDKMock;

	@Test
	void synchronizeToken_ShouldReturnCurrentSellerModel_WhenSellerBankAccountDetailsAreNull() {

		final SellerModel originalSellerModel = SellerModel.builder().build();

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result).isEqualTo(originalSellerModel);
	}

	@Test
	void synchronizeToken_ShouldReturnCurrentSellerModel_WhenSellerBankAccountNumberIsBlank() {
		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber("").build()).build();

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result).isEqualTo(originalSellerModel);
	}

	@Test
	void synchronizeToken_ShouldUpdateMiraklToken_WhenBankAccountMatchIsFound_AndMiraklBankAccountTokenIsNull() {
		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber(BANK_ACCOUNT_NUMBER).build()).build();

		final HyperwalletBankAccount hyperwalletBankAccount1 = new HyperwalletBankAccount();
		hyperwalletBankAccount1.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		final HyperwalletBankAccount hyperwalletBankAccount2 = new HyperwalletBankAccount();
		hyperwalletBankAccount2.setToken(BANK_ACCOUNT_TOKEN_VALUE_2);

		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccountList = new HyperwalletList<>();
		hyperwalletBankAccountList.setData(List.of(hyperwalletBankAccount1, hyperwalletBankAccount2));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(hyperwalletBankAccountList);
		when(miraklBankAccountMatcherMock.findExactOrCompatibleMatch(hyperwalletBankAccountList.getData(),
				originalSellerModel.getBankAccountDetails())).thenReturn(Optional.of(hyperwalletBankAccount2));

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result.getBankAccountDetails().getToken()).isEqualTo(BANK_ACCOUNT_TOKEN_VALUE_2);
		verify(miraklBankAccountExtractServiceMock, times(1)).updateBankAccountToken(originalSellerModel,
				hyperwalletBankAccount2);
	}

	@Test
	void synchronizeToken_ShouldUpdateMiraklToken_WhenBankAccountMatchIsFound_AndMiraklBankAccountTokenIsNotNull() {
		final SellerModel originalSellerModel = SellerModel
				.builder().token(SELLER_TOKEN_VALUE).programToken(PROGRAM_TOKEN).bankAccountDetails(BankAccountModel
						.builder().token(BANK_ACCOUNT_TOKEN_VALUE).bankAccountNumber(BANK_ACCOUNT_NUMBER).build())
				.build();

		final HyperwalletBankAccount hyperwalletBankAccount1 = new HyperwalletBankAccount();
		hyperwalletBankAccount1.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		final HyperwalletBankAccount hyperwalletBankAccount2 = new HyperwalletBankAccount();
		hyperwalletBankAccount2.setToken(BANK_ACCOUNT_TOKEN_VALUE_2);

		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccountList = new HyperwalletList<>();
		hyperwalletBankAccountList.setData(List.of(hyperwalletBankAccount1, hyperwalletBankAccount2));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(hyperwalletBankAccountList);
		when(miraklBankAccountMatcherMock.findExactOrCompatibleMatch(hyperwalletBankAccountList.getData(),
				originalSellerModel.getBankAccountDetails())).thenReturn(Optional.of(hyperwalletBankAccount2));

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result.getBankAccountDetails().getToken()).isEqualTo(BANK_ACCOUNT_TOKEN_VALUE_2);
		verify(miraklBankAccountExtractServiceMock, times(1)).updateBankAccountToken(originalSellerModel,
				hyperwalletBankAccount2);
	}

	@Test
	void synchronizeToken_ShouldSetMiraklTokenToNull_WhenHyperwalletBankAccountsIsEmpty_AndMiraklBankAccountTokenIsNotNull() {
		final SellerModel originalSellerModel = SellerModel
				.builder().token(SELLER_TOKEN_VALUE).programToken(PROGRAM_TOKEN).bankAccountDetails(BankAccountModel
						.builder().token(BANK_ACCOUNT_TOKEN_VALUE).bankAccountNumber(BANK_ACCOUNT_NUMBER).build())
				.build();

		final HyperwalletBankAccount hyperwalletBankAccount1 = new HyperwalletBankAccount();
		hyperwalletBankAccount1.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		final HyperwalletBankAccount hyperwalletBankAccount2 = new HyperwalletBankAccount();
		hyperwalletBankAccount2.setToken(BANK_ACCOUNT_TOKEN_VALUE_2);

		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccountList = new HyperwalletList<>();
		hyperwalletBankAccountList.setData(null);

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(hyperwalletBankAccountList);
		when(miraklBankAccountMatcherMock.findExactOrCompatibleMatch(List.of(),
				originalSellerModel.getBankAccountDetails())).thenReturn(Optional.empty());

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result.getBankAccountDetails().getToken()).isNull();
		verify(miraklBankAccountExtractServiceMock, times(1)).updateBankAccountToken(eq(originalSellerModel),
				argThat(arg -> arg.getToken() == null));
	}

	@Test
	void synchronizeToken_ShouldSetMiraklTokenToNull_WhenBankAccountMatchIsNotFound_AndMiraklBankAccountTokenIsNotNull() {
		final SellerModel originalSellerModel = SellerModel
				.builder().token(SELLER_TOKEN_VALUE).programToken(PROGRAM_TOKEN).bankAccountDetails(BankAccountModel
						.builder().token(BANK_ACCOUNT_TOKEN_VALUE).bankAccountNumber(BANK_ACCOUNT_NUMBER).build())
				.build();

		final HyperwalletBankAccount hyperwalletBankAccount1 = new HyperwalletBankAccount();
		hyperwalletBankAccount1.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		final HyperwalletBankAccount hyperwalletBankAccount2 = new HyperwalletBankAccount();
		hyperwalletBankAccount2.setToken(BANK_ACCOUNT_TOKEN_VALUE_2);

		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccountList = new HyperwalletList<>();
		hyperwalletBankAccountList.setData(List.of(hyperwalletBankAccount1, hyperwalletBankAccount2));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(hyperwalletBankAccountList);
		when(miraklBankAccountMatcherMock.findExactOrCompatibleMatch(hyperwalletBankAccountList.getData(),
				originalSellerModel.getBankAccountDetails())).thenReturn(Optional.empty());

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result.getBankAccountDetails().getToken()).isNull();
		verify(miraklBankAccountExtractServiceMock, times(1)).updateBankAccountToken(eq(originalSellerModel),
				argThat(arg -> arg.getToken() == null));
	}

	@Test
	void synchronizeToken_ShouldNotUpdateAnything_WhenBankAccountMatchIsNotFound_AndMiraklBankAccountTokenIsNull() {
		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber(BANK_ACCOUNT_NUMBER).build()).build();

		final HyperwalletBankAccount hyperwalletBankAccount1 = new HyperwalletBankAccount();
		hyperwalletBankAccount1.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		final HyperwalletBankAccount hyperwalletBankAccount2 = new HyperwalletBankAccount();
		hyperwalletBankAccount2.setToken(BANK_ACCOUNT_TOKEN_VALUE_2);

		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccountList = new HyperwalletList<>();
		hyperwalletBankAccountList.setData(List.of(hyperwalletBankAccount1, hyperwalletBankAccount2));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(hyperwalletBankAccountList);
		when(miraklBankAccountMatcherMock.findExactOrCompatibleMatch(hyperwalletBankAccountList.getData(),
				originalSellerModel.getBankAccountDetails())).thenReturn(Optional.empty());

		final SellerModel result = testObj.synchronizeToken(originalSellerModel);

		assertThat(result).isEqualTo(originalSellerModel);
		assertThat(result.getBankAccountDetails().getToken()).isNull();
		verify(miraklBankAccountExtractServiceMock, times(0)).updateBankAccountToken(any(), any());
	}

	@Test
	void synchronizeToken_ShouldThrowHMCHyperwalletAPIException_WhenHWRequestThrowAHyperwalletException() {
		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber(BANK_ACCOUNT_NUMBER).build()).build();

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE))
				.thenThrow(new HyperwalletException("Something went wrong"));

		assertThatThrownBy(() -> testObj.synchronizeToken(originalSellerModel))
				.isInstanceOf(HMCHyperwalletAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Hyperwallet API");
	}

	@Test
	void synchronizeToken_ShouldThrowHMCMiraklAPIException_WhenMiraklRequestThrowAMiraklApiException() {
		final SellerModel originalSellerModel = SellerModel.builder().token(SELLER_TOKEN_VALUE)
				.programToken(PROGRAM_TOKEN)
				.bankAccountDetails(BankAccountModel.builder().bankAccountNumber(BANK_ACCOUNT_NUMBER).build()).build();

		final HyperwalletBankAccount hyperwalletBankAccount = new HyperwalletBankAccount();
		hyperwalletBankAccount.setToken(BANK_ACCOUNT_TOKEN_VALUE);
		final HyperwalletList<HyperwalletBankAccount> hyperwalletBankAccountList = new HyperwalletList<>();
		hyperwalletBankAccountList.setData(List.of(hyperwalletBankAccount));

		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN))
				.thenReturn(hyperwalletSDKMock);
		when(hyperwalletSDKMock.listBankAccounts(SELLER_TOKEN_VALUE)).thenReturn(hyperwalletBankAccountList);
		when(miraklBankAccountMatcherMock.findExactOrCompatibleMatch(hyperwalletBankAccountList.getData(),
				originalSellerModel.getBankAccountDetails())).thenReturn(Optional.of(hyperwalletBankAccount));

		doThrow(MiraklApiException.class).when(miraklBankAccountExtractServiceMock)
				.updateBankAccountToken(originalSellerModel, hyperwalletBankAccount);

		assertThatThrownBy(() -> testObj.synchronizeToken(originalSellerModel))
				.isInstanceOf(HMCMiraklAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Mirakl API");
	}

}
