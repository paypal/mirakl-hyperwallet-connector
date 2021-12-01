package com.paypal.reports.reportsextract.model;

import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HmcMiraklTransactionLineTest {

	private static final String ORDER_ID = "orderId";

	private static final String SELLER_ID = "sellerId";

	private static final String MIRAKL_TRANSACTION_LINE_ID = "miraklTransactionLineId";

	private static final String MIRAKL_TRANSACTION_TYPE = "miraklTransactionType";

	private static final String PAYMENT_TRANSACTION_ID = "paymentTransactionId";

	@Test
	void builder_shouldCreateMiraklTransactionLineObject() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
		final HmcMiraklTransactionLine result = HmcMiraklTransactionLine.builder()
				.creditAmount(BigDecimal.ONE)
				.debitAmount(BigDecimal.TEN)
				.orderId(ORDER_ID)
				.sellerId(SELLER_ID)
				.transactionLineId(MIRAKL_TRANSACTION_LINE_ID)
				.transactionTime(now)
				.transactionType(MIRAKL_TRANSACTION_TYPE)
				.transactionNumber(PAYMENT_TRANSACTION_ID)
				.build();
		//@formatter:on

		assertThat(result.getCreditAmount()).isEqualTo(BigDecimal.ONE);
		assertThat(result.getDebitAmount()).isEqualTo(BigDecimal.TEN);
		assertThat(result.getOrderId()).isEqualTo(ORDER_ID);
		assertThat(result.getSellerId()).isEqualTo(SELLER_ID);
		assertThat(result.getTransactionLineId()).isEqualTo(MIRAKL_TRANSACTION_LINE_ID);
		assertThat(result.getTransactionTime()).isEqualTo(now);
		assertThat(result.getTransactionType()).isEqualTo(MIRAKL_TRANSACTION_TYPE);
		assertThat(result.getTransactionNumber()).isEqualTo(PAYMENT_TRANSACTION_ID);
	}

}
