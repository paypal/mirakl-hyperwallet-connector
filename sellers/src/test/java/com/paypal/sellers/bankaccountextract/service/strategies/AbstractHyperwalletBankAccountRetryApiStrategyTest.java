package com.paypal.sellers.bankaccountextract.service.strategies;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.FailedEntityInformationService;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractHyperwalletBankAccountRetryApiStrategyTest {

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Spy
	@InjectMocks
	private MyAbstractHyperwalletBankAccountRetryApiStrategy testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutorMock;

	@Mock
	private HyperwalletBankAccount hyperwalletBankAccountMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private IOException ioExceptionMock;

	@Mock
	private HyperwalletException hyperwalletExceptionMock;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Test
	void execute_shouldcallMiraklAPI() {
		when(sellerModelToHyperwalletBankAccountStrategyExecutorMock.execute(sellerModelMock))
				.thenReturn(hyperwalletBankAccountMock);
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		doNothing().when(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.FALSE);
		testObj.execute(sellerModelMock);

		verify(sellerModelToHyperwalletBankAccountStrategyExecutorMock).execute(sellerModelMock);
		verify(testObj).callMiraklAPI(HYPERWALLET_PROGRAM, hyperwalletBankAccountMock);

	}

	@Test
	void execute_shouldSendEmailNotificationHyperwalletExceptionIsThrown() {
		final var hyperwalletException = new HyperwalletException("Something went wrong");
		when(sellerModelMock.getClientUserId()).thenReturn("2001");
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(sellerModelToHyperwalletBankAccountStrategyExecutorMock.execute(sellerModelMock))
				.thenReturn(hyperwalletBankAccountMock);
		doNothing().when(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.FALSE);
		doThrow(hyperwalletException).when(testObj).callMiraklAPI(HYPERWALLET_PROGRAM, hyperwalletBankAccountMock);

		testObj.execute(sellerModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected when creating or updating bank account in Hyperwallet",
				String.format(
						ERROR_MESSAGE_PREFIX + "Bank account not created or updated for seller with clientId [%s]%n%s",
						"2001", HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
		verify(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.FALSE);
	}

	@Test
	void execute_shouldSendEmailNotificationAndIncludeSellerIntoRetryProcessWhenIOExceptionIsThrown() {
		when(hyperwalletExceptionMock.getCause()).thenReturn(ioExceptionMock);
		when(sellerModelMock.getClientUserId()).thenReturn("2001");
		when(sellerModelMock.getHyperwalletProgram()).thenReturn(HYPERWALLET_PROGRAM);
		when(sellerModelToHyperwalletBankAccountStrategyExecutorMock.execute(sellerModelMock))
				.thenReturn(hyperwalletBankAccountMock);
		doThrow(hyperwalletExceptionMock).when(testObj).callMiraklAPI(HYPERWALLET_PROGRAM, hyperwalletBankAccountMock);
		doNothing().when(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.TRUE);

		testObj.execute(sellerModelMock);

		verify(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.TRUE);
	}

	private static class MyAbstractHyperwalletBankAccountRetryApiStrategy
			extends AbstractHyperwalletBankAccountRetryApiStrategy {

		public MyAbstractHyperwalletBankAccountRetryApiStrategy(
				final FailedEntityInformationService<FailedBankAccountInformation> failedEntityInformationService,
				final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor,
				final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
			super(failedEntityInformationService, sellerModelToHyperwalletBankAccountStrategyExecutor,
					hyperwalletSDKService, mailNotificationUtil);
		}

		@Override
		protected HyperwalletBankAccount callMiraklAPI(final String hyperwalletProgram,
				final HyperwalletBankAccount hyperwalletBankAccount) {
			return null;
		}

		@Override
		public boolean isApplicable(final SellerModel source) {
			return false;
		}

	}

}
