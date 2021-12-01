package com.paypal.reports.reportsextract.model;

import com.paypal.infrastructure.util.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HmcFinancialReportLineTest {

	private static final String BRAIN_TREE_ORDER_ID = "braintreeOrderId";

	private static final String ORDER_ID = "orderId";

	private static final String SELLER_ID = "sellerId";

	private static final String MIRAKL_TRANSACTION_LINE_ID = "miraklTransactionLineId";

	private static final String MIRAKL_TRANSACTION_TYPE = "miraklTransactionType";

	private static final String PAYMENT_TRANSACTION_ID = "paymentTransactionId";

	@Test
	void builder_shouldCreateFinancialReportTransactionLineObject() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
		final HmcFinancialReportLine result = HmcFinancialReportLine.builder()
				.braintreeCommerceOrderId(BRAIN_TREE_ORDER_ID)
				.miraklOrderId(ORDER_ID)
				.miraklSellerId(SELLER_ID)
				.miraklTransactionLineId(MIRAKL_TRANSACTION_LINE_ID)
				.miraklTransactionTime(now)
				.miraklTransactionType(MIRAKL_TRANSACTION_TYPE)
				.braintreeAmount(BigDecimal.ONE)
				.currencyIsoCode("EUR")
				.braintreeTransactionId(PAYMENT_TRANSACTION_ID)
				.braintreeTransactionTime(now)
				.build();
		//@formatter:on

		assertThat(result.getBraintreeCommerceOrderId()).isEqualTo(BRAIN_TREE_ORDER_ID);
		assertThat(result.getMiraklOrderId()).isEqualTo(ORDER_ID);
		assertThat(result.getMiraklSellerId()).isEqualTo(SELLER_ID);
		assertThat(result.getMiraklTransactionLineId()).isEqualTo(MIRAKL_TRANSACTION_LINE_ID);
		assertThat(result.getMiraklTransactionTime()).isEqualTo(now);
		assertThat(result.getMiraklTransactionType()).isEqualTo(MIRAKL_TRANSACTION_TYPE);
		assertThat(result.getBraintreeAmount()).isEqualTo(BigDecimal.ONE);
		assertThat(result.getBraintreeTransactionId()).isEqualTo(PAYMENT_TRANSACTION_ID);
		assertThat(result.getBraintreeTransactionTime()).isEqualTo(now);
		assertThat(result.getCurrencyIsoCode()).isEqualTo("EUR");
	}

}
