package com.paypal.sellers.bankaccountextraction.services.strategies;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.mirakl.client.core.exception.MiraklApiException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractHyperwalletBankAccountStrategyTest {

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Spy
	@InjectMocks
	private MyAbstractHyperwalletBankAccountStrategy testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutorMock;

	@Mock
	private HyperwalletBankAccount hyperwalletBankAccountMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private HyperwalletException hyperwalletExceptionMock;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Test
	void execute_shouldCallHyperwalletAPI() {
		when(sellerModelToHyperwalletBankAccountStrategyExecutorMock.execute(sellerModelMock))
				.thenReturn(hyperwalletBankAccountMock);
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		testObj.execute(sellerModelMock);

		verify(sellerModelToHyperwalletBankAccountStrategyExecutorMock).execute(sellerModelMock);
		verify(testObj).callHyperwalletAPI(HYPERWALLET_PROGRAM, hyperwalletBankAccountMock);
	}

	@Test
	void execute_shouldSendEmailNotificationHyperwalletExceptionIsThrown() {
		final HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		when(sellerModelMock.getClientUserId()).thenReturn("2001");
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(sellerModelToHyperwalletBankAccountStrategyExecutorMock.execute(sellerModelMock))
				.thenReturn(hyperwalletBankAccountMock);
		doThrow(hyperwalletException).when(testObj).callHyperwalletAPI(HYPERWALLET_PROGRAM, hyperwalletBankAccountMock);

		AssertionsForClassTypes.assertThatThrownBy(() -> testObj.execute(sellerModelMock))
				.isInstanceOf(HMCHyperwalletAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Hyperwallet API");

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected when creating or updating bank account in Hyperwallet",
				(ERROR_MESSAGE_PREFIX + "Bank account not created or updated for seller with clientId [%s]%n%s")
						.formatted("2001", HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void execute_ShouldThrowMiraklApiException_WhenMiraklRequestThrowsAnHMCMiraklAPIException() {
		when(sellerModelMock.getClientUserId()).thenReturn("2001");
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(sellerModelToHyperwalletBankAccountStrategyExecutorMock.execute(sellerModelMock))
				.thenReturn(hyperwalletBankAccountMock);
		doThrow(MiraklApiException.class).when(testObj).callHyperwalletAPI(HYPERWALLET_PROGRAM,
				hyperwalletBankAccountMock);

		AssertionsForClassTypes.assertThatThrownBy(() -> testObj.execute(sellerModelMock))
				.isInstanceOf(HMCMiraklAPIException.class)
				.hasMessageContaining("An error has occurred while invoking Mirakl API");
	}

	private static class MyAbstractHyperwalletBankAccountStrategy extends AbstractHyperwalletBankAccountStrategy {

		public MyAbstractHyperwalletBankAccountStrategy(
				final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor,
				final UserHyperwalletSDKService userHyperwalletSDKService,
				final MailNotificationUtil mailNotificationUtil) {
			super(sellerModelToHyperwalletBankAccountStrategyExecutor, userHyperwalletSDKService, mailNotificationUtil);
		}

		@Override
		protected HyperwalletBankAccount callHyperwalletAPI(final String hyperwalletProgram,
				final HyperwalletBankAccount hyperwalletBankAccount) {
			return null;
		}

		@Override
		public boolean isApplicable(final SellerModel source) {
			return false;
		}

	}

}
