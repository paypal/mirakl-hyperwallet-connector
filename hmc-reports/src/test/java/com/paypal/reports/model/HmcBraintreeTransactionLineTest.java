package com.paypal.reports.model;

import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HmcBraintreeTransactionLineTest {

	private static final String BRAIN_TREE_ORDER_ID = "braintreeOrderId";

	private static final String PAYMENT_TRANSACTION_ID = "paymentTransactionId";

	@Test
	void builder_shouldCreateBraintreeTransactionLineObject() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
		final HmcBraintreeTransactionLine result = HmcBraintreeTransactionLine.builder()
				.orderId(BRAIN_TREE_ORDER_ID)
				.amount(BigDecimal.ONE)
				.paymentTransactionTime(now)
				.paymentTransactionId(PAYMENT_TRANSACTION_ID)
				.build();
		//@formatter:on

		assertThat(result.getAmount()).isEqualTo(BigDecimal.ONE);
		assertThat(result.getOrderId()).isEqualTo(BRAIN_TREE_ORDER_ID);
		assertThat(result.getPaymentTransactionTime()).isEqualTo(now);
		assertThat(result.getPaymentTransactionId()).isEqualTo(PAYMENT_TRANSACTION_ID);
	}

}
