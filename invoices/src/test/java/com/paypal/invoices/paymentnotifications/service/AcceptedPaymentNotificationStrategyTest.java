package com.paypal.invoices.paymentnotifications.service;

import com.mirakl.client.mmp.core.MiraklMarketplacePlatformFrontOperatorApi;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.invoice.MiraklAccountingDocumentPaymentConfirmation;
import com.mirakl.client.mmp.request.invoice.MiraklConfirmAccountingDocumentPaymentRequest;
import com.paypal.infrastructure.constants.HyperWalletConstants;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.invoices.infraestructure.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptedPaymentNotificationStrategyTest {

	private static final String COMPLETED = "COMPLETED";

	private static final String NOT_COMPLETED = "NOT_COMPLETED";

	private static final String AMOUNT = "123.45";

	private static final String INVOICE_ID = "ID_1234";

	private static final String CURRENCY_EUR_ISO_CODE = "EUR";

	private static final String CREATED_ON = "2021-01-11T00:00:00";

	@InjectMocks
	private AcceptedPaymentNotificationStrategy testObj;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Mock
	private PaymentNotificationConfig paymentNotificationConfigMock;

	@Mock
	private MiraklMarketplacePlatformFrontOperatorApi miraklMarketplacePlatformFrontOperatorApiMock;

	@Captor
	ArgumentCaptor<MiraklConfirmAccountingDocumentPaymentRequest> miraklConfirmAccountingDocumentPaymentRequestArgumentCaptor;

	@Test
	void execute_shouldSendConfirmationPaymentToMirakl() {
		when(paymentNotificationBodyModelMock.getAmount()).thenReturn(AMOUNT);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn(INVOICE_ID);
		when(paymentNotificationBodyModelMock.getCurrency()).thenReturn(CURRENCY_EUR_ISO_CODE);
		when(paymentNotificationBodyModelMock.getCreatedOn()).thenReturn(CREATED_ON);

		testObj.execute(paymentNotificationBodyModelMock);

		verify(miraklMarketplacePlatformFrontOperatorApiMock).confirmAccountingDocumentPayment(
				miraklConfirmAccountingDocumentPaymentRequestArgumentCaptor.capture());

		final MiraklConfirmAccountingDocumentPaymentRequest miraklConfirmAccountingDocumentPaymentRequest = miraklConfirmAccountingDocumentPaymentRequestArgumentCaptor
				.getValue();
		final List<MiraklAccountingDocumentPaymentConfirmation> accountingDocuments = miraklConfirmAccountingDocumentPaymentRequest
				.getAccountingDocuments();

		assertThat(miraklConfirmAccountingDocumentPaymentRequest.getAccountingDocuments()).hasSize(1);
		assertThat(accountingDocuments.get(0).getAmount()).isEqualTo(AMOUNT);
		assertThat(accountingDocuments.get(0).getInvoiceId()).isEqualTo(INVOICE_ID);
		assertThat(accountingDocuments.get(0).getCurrencyIsoCode()).isEqualTo(MiraklIsoCurrencyCode.EUR);
		assertThat(accountingDocuments.get(0).getTransactionDate()).isEqualTo(DateUtil.convertToDate(CREATED_ON,
				HyperWalletConstants.HYPERWALLET_DATE_FORMAT, TimeZone.getTimeZone("UTC")));
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNull() {
		final boolean result = testObj.isApplicable(null);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentNotificationBodyModelIsAcceptedAndIsNotOperatorFee() {
		when(paymentNotificationConfigMock.getAcceptedStatuses()).thenReturn(List.of(COMPLETED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(COMPLETED);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("290320");

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsAcceptedAndIsOperatorFee() {
		when(paymentNotificationConfigMock.getAcceptedStatuses()).thenReturn(List.of(COMPLETED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(COMPLETED);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("290320-operatorFee");

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNotAccepted() {
		when(paymentNotificationConfigMock.getAcceptedStatuses()).thenReturn(List.of(COMPLETED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(NOT_COMPLETED);

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

}
