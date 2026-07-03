package com.paypal.invoices.paymentnotifications.services;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCyclePaymentConfirmation;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklConfirmSellerBillingCyclePaymentRequest;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.invoices.paymentnotifications.configuration.PaymentNotificationConfig;
import com.paypal.invoices.paymentnotifications.model.PaymentNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptedPaymentNotificationStrategyTest {

	private static final String COMPLETED = "COMPLETED";

	private static final String NOT_COMPLETED = "NOT_COMPLETED";

	private static final String AMOUNT = "123.45";

	private static final String CLIENT_PAYMENT_ID = "123e4567-e89b-12d3-a456-426614174000";

	private static final String CURRENCY_EUR_ISO_CODE = "EUR";

	private static final String CREATED_ON = "2021-01-11T00:00:00";

	@InjectMocks
	private AcceptedPaymentNotificationStrategy testObj;

	@Mock
	private PaymentNotificationBodyModel paymentNotificationBodyModelMock;

	@Mock
	private PaymentNotificationConfig paymentNotificationConfigMock;

	@Mock
	private MiraklClient miraklClientMock;

	@Captor
	ArgumentCaptor<MiraklConfirmSellerBillingCyclePaymentRequest> miraklConfirmRequestCaptor;

	@Test
	void execute_shouldSendConfirmationPaymentToMirakl() {
		when(paymentNotificationBodyModelMock.getAmount()).thenReturn(AMOUNT);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn(CLIENT_PAYMENT_ID);
		when(paymentNotificationBodyModelMock.getCurrency()).thenReturn(CURRENCY_EUR_ISO_CODE);
		when(paymentNotificationBodyModelMock.getCreatedOn()).thenReturn(CREATED_ON);

		testObj.execute(paymentNotificationBodyModelMock);

		verify(miraklClientMock).confirmSellerBillingCyclePayment(miraklConfirmRequestCaptor.capture());

		final MiraklConfirmSellerBillingCyclePaymentRequest request = miraklConfirmRequestCaptor.getValue();
		final Collection<MiraklSellerBillingCyclePaymentConfirmation> confirmations = request.getSellerBillingCycles();

		assertThat(confirmations).hasSize(1);
		final MiraklSellerBillingCyclePaymentConfirmation confirmation = confirmations.iterator().next();
		assertThat(confirmation.getId()).isEqualTo(UUID.fromString(CLIENT_PAYMENT_ID));
		assertThat(confirmation.getAmountTransferredToSeller()).isEqualTo(new BigDecimal(AMOUNT));
		assertThat(confirmation.getCurrencyIsoCode()).isEqualTo(MiraklIsoCurrencyCode.EUR);
		assertThat(confirmation.getState()).isEqualTo(MiraklPayOutState.PAID);
		assertThat(confirmation.getTransactionDate()).isEqualTo(Instant.parse(CREATED_ON + "Z"));
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNull() {
		final boolean result = testObj.isApplicable(null);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnTrue_whenPaymentNotificationBodyModelIsAcceptedAndIsNotOperatorFee() {
		when(paymentNotificationConfigMock.getAcceptedStatuses()).thenReturn(Set.of(COMPLETED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(COMPLETED);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("290320");

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsAcceptedAndIsOperatorFee() {
		when(paymentNotificationConfigMock.getAcceptedStatuses()).thenReturn(Set.of(COMPLETED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(COMPLETED);
		when(paymentNotificationBodyModelMock.getClientPaymentId()).thenReturn("290320-operatorFee");

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalse_whenPaymentNotificationBodyModelIsNotAccepted() {
		when(paymentNotificationConfigMock.getAcceptedStatuses()).thenReturn(Set.of(COMPLETED));
		when(paymentNotificationBodyModelMock.getStatus()).thenReturn(NOT_COMPLETED);

		final boolean result = testObj.isApplicable(paymentNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

}
