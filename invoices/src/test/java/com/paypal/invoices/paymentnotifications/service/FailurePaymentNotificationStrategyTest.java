package com.paypal.invoices.paymentnotifications.service;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailurePaymentNotificationStrategyTest {

	private static final String FAILED = "FAILED";

	private static final String NOT_FAILED = "NOT_FAILED";

	@InjectMocks
	private FailurePaymentNotificationStrategy testObj;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Mock
	private PaymentNotificationConfig paymentNotificationConfigMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private HyperwalletSDKUserService hyperwalletSDKUserServiceMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Test
	void executeProcessPaymentNotification_shouldSendEmailNotification_WhenPaymentNotificationIsInAFailureStatus_AndThereIsBusinessShopInfo() {
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn("FAILED");
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("ClientPaymentID");
		when(paymentNotificationBodyModelMock.getProgramToken()).thenReturn("programToken");
		when(paymentNotificationBodyModelMock.getDestinationToken()).thenReturn("destinationToken");
		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken("programToken"))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.getUser("destinationToken")).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getBusinessName()).thenReturn("BUSINESS_SHOP");
		when(hyperwalletUserMock.getClientUserId()).thenReturn("ClientUserId");

		testObj.execute(paymentNotificationBodyModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Problem while processing payment [ClientPaymentID] of shop BUSINESS_SHOP with id [ClientUserId]",
				"A problem has been detected while processing the payment corresponding to the invoice [ClientPaymentID] of the shop BUSINESS_SHOP with id [ClientUserId].\n"
						+ "The status received for the payment is [FAILED].\n"
						+ "For more information please consult your Hyperwallet dashboard.");
	}

	@Test
	void executeProcessPaymentNotification_shouldSendEmailNotification_WhenPaymentNotificationIsInAFailureStatus_AndThereIsIndividualShopInfo() {
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn("FAILED");
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("ClientPaymentID");
		when(paymentNotificationBodyModelMock.getProgramToken()).thenReturn("programToken");
		when(paymentNotificationBodyModelMock.getDestinationToken()).thenReturn("destinationToken");
		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken("programToken"))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.getUser("destinationToken")).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getFirstName()).thenReturn("First");
		when(hyperwalletUserMock.getLastName()).thenReturn("Last");
		when(hyperwalletUserMock.getClientUserId()).thenReturn("ClientUserId");

		testObj.execute(paymentNotificationBodyModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Problem while processing payment [ClientPaymentID] of shop First Last with id [ClientUserId]",
				"A problem has been detected while processing the payment corresponding to the invoice [ClientPaymentID] of the shop First Last with id [ClientUserId].\n"
						+ "The status received for the payment is [FAILED].\n"
						+ "For more information please consult your Hyperwallet dashboard.");
	}

	@Test
	void executeProcessPaymentNotification_shouldSendEmailNotification_WhenPaymentNotificationIsInAFailureStatus_AndThereIsNoShopInfo() {
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn("FAILED");
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("ClientPaymentID");
		when(paymentNotificationBodyModelMock.getProgramToken()).thenReturn("programToken");
		when(paymentNotificationBodyModelMock.getDestinationToken()).thenReturn("destinationToken");
		when(hyperwalletSDKUserServiceMock.getHyperwalletInstanceByProgramToken("programToken"))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.getUser("destinationToken")).thenThrow(HyperwalletException.class);

		testObj.execute(paymentNotificationBodyModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Problem while processing payment [ClientPaymentID]",
				"A problem has been detected while processing the payment corresponding to the invoice [ClientPaymentID].\n"
						+ "The status received for the payment is [FAILED].\n"
						+ "For more information please consult your Hyperwallet dashboard.");
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentNotificationBodyModelHasFailed() {
		when(paymentNotificationConfigMock.getFailureStatuses()).thenReturn(Set.of(FAILED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(FAILED);

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelHasNotFailed() {
		when(paymentNotificationConfigMock.getFailureStatuses()).thenReturn(Set.of(FAILED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(NOT_FAILED);

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNull() {
		final boolean result = testObj.isApplicable(null);

		assertThat(result).isFalse();
	}

}
