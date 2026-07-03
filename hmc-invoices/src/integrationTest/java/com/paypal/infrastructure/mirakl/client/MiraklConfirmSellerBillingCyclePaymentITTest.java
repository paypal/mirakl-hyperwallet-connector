package com.paypal.infrastructure.mirakl.client;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCyclePaymentConfirmation;
import com.mirakl.client.mmp.operator.request.payment.sellerbillingcycle.MiraklConfirmSellerBillingCyclePaymentRequest;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

class MiraklConfirmSellerBillingCyclePaymentITTest extends AbstractMockEnabledIntegrationTest {

	private static final UUID SELLER_BILLING_CYCLE_ID = UUID.fromString("a5b77ee5-f5c2-4efc-ab94-8366ee781598");

	private static final BigDecimal AMOUNT = new BigDecimal("123.45");

	private static final Instant TRANSACTION_DATE = Instant.parse("2021-01-11T00:00:00Z");

	@Autowired
	private MiraklClient miraklClient;

	@Test
	void shouldConfirmSellerBillingCyclePayment() {
		miraklConfirmSellerBillingCyclePaymentEndpointMock.confirmSellerBillingCyclePayment();

		final MiraklSellerBillingCyclePaymentConfirmation confirmation = new MiraklSellerBillingCyclePaymentConfirmation();
		confirmation.setId(SELLER_BILLING_CYCLE_ID);
		confirmation.setAmountTransferredToSeller(AMOUNT);
		confirmation.setCurrencyIsoCode(MiraklIsoCurrencyCode.EUR);
		confirmation.setState(MiraklPayOutState.PAID);
		confirmation.setTransactionDate(TRANSACTION_DATE);

		final MiraklConfirmSellerBillingCyclePaymentRequest request = new MiraklConfirmSellerBillingCyclePaymentRequest(
				List.of(confirmation));

		assertThatCode(() -> miraklClient.confirmSellerBillingCyclePayment(request)).doesNotThrowAnyException();
	}

}
