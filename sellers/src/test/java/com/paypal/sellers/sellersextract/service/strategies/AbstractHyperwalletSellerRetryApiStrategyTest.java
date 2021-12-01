package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.entity.FailedSellersInformation;
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
class AbstractHyperwalletSellerRetryApiStrategyTest {

	@Spy
	@InjectMocks
	private MyAbstractHyperwalletSellerRetryApiStrategy testObj;

	@Mock
	private Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverterMock;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private IOException ioExceptionMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private HyperwalletException hyperwalletExceptionMock;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@Test
	void execute_shouldCallMiraklAPI() {
		when(sellerModelHyperwalletUserConverterMock.convert(sellerModelMock)).thenReturn(hyperwalletUserMock);
		doNothing().when(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.FALSE);

		testObj.execute(sellerModelMock);

		verify(sellerModelHyperwalletUserConverterMock).convert(sellerModelMock);
		verify(testObj).createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(hyperwalletUserMock);
	}

	@Test
	void execute_shouldSendEmailNotificationHyperwalletExceptionIsThrown() {
		final HyperwalletException hyperwalletException = new HyperwalletException("Something went wrong");
		when(sellerModelMock.getClientUserId()).thenReturn("2001");
		when(sellerModelHyperwalletUserConverterMock.convert(sellerModelMock)).thenReturn(hyperwalletUserMock);
		doNothing().when(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.FALSE);
		doThrow(hyperwalletException).when(testObj)
				.createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(hyperwalletUserMock);

		testObj.execute(sellerModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected when creating or updating seller in Hyperwallet",
				String.format(ERROR_MESSAGE_PREFIX + "Seller not created or updated with clientId [%s]%n%s", "2001",
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
		verify(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.FALSE);
	}

	@Test
	void execute_shouldSendEmailNotificationAndIncludeSellerIntoRetryProcessWhenIOExceptionIsThrown() {
		when(hyperwalletExceptionMock.getCause()).thenReturn(ioExceptionMock);
		when(sellerModelMock.getClientUserId()).thenReturn("2001");
		when(sellerModelHyperwalletUserConverterMock.convert(sellerModelMock)).thenReturn(hyperwalletUserMock);
		doThrow(hyperwalletExceptionMock).when(testObj)
				.createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(hyperwalletUserMock);
		doNothing().when(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.TRUE);

		testObj.execute(sellerModelMock);

		verify(testObj).callToIncludeIntoRetryProcess(sellerModelMock, Boolean.TRUE);
	}

	private static class MyAbstractHyperwalletSellerRetryApiStrategy extends AbstractHyperwalletSellerRetryApiStrategy {

		protected MyAbstractHyperwalletSellerRetryApiStrategy(
				final FailedEntityInformationService<FailedSellersInformation> failedEntityInformationService,
				final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
				final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
			super(failedEntityInformationService, sellerModelHyperwalletUserConverter, hyperwalletSDKService,
					mailNotificationUtil);
		}

		@Override
		protected HyperwalletUser createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(
				final HyperwalletUser hyperwalletUser) {
			return null;
		}

		@Override
		public boolean isApplicable(final SellerModel source) {
			return false;
		}

	}

}
