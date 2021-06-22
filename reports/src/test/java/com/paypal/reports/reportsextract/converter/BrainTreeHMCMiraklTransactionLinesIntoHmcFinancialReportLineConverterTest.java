package com.paypal.reports.reportsextract.converter;

import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BrainTreeHMCMiraklTransactionLinesIntoHmcFinancialReportLineConverterTest {

	private static final String EUR_CURRENCY_ISO_CODE = "EUR";

	@InjectMocks
	private BrainTreeMiraklTransactionLinesIntoFinancialReportLineConverter testObj;

	private static final String BRAINTREE_ORDER_ID = "braintreeOrderId";

	private static final String MIRAKL_ORDER_ID = "miraklOrderId";

	private static final String PAYMENT_TRANSACTION_ID = "payamentTransactionId";

	private static final String MIRAKL_SELLER_ID = "miraklSellerId";

	private static final String MIRAKL_TRANSACTION_LINE_ID = "miraklTransactionLineId";

	private static final String MIRAKL_TRANSACTION_TYPE = "miraklTransactionType";

	@Test
	void convert_shouldConvertBraintreeTransactionLineAndMiraklTransactionLineIntoFinancialReportLineWhenAllAttributesAreFilled() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
        final HmcBraintreeTransactionLine HmcBraintreeTransactionLineStub = HmcBraintreeTransactionLine.builder()
                .orderId(BRAINTREE_ORDER_ID)
                .amount(BigDecimal.TEN)
				.currencyIsoCode(EUR_CURRENCY_ISO_CODE)
                .paymentTransactionId(PAYMENT_TRANSACTION_ID)
                .paymentTransactionTime(now)
                .build();
        //@formatter:on

		//@formatter:off
        final HmcMiraklTransactionLine HmcMiraklTransactionLineStub = HmcMiraklTransactionLine.builder()
                .orderId(MIRAKL_ORDER_ID)
                .sellerId(MIRAKL_SELLER_ID)
                .transactionLineId(MIRAKL_TRANSACTION_LINE_ID)
                .transactionTime(now)
                .transactionType(MIRAKL_TRANSACTION_TYPE)
				.amount(BigDecimal.ONE)
				.debitAmount(BigDecimal.ZERO)
				.creditAmount(BigDecimal.TEN)
                .transactionNumber(PAYMENT_TRANSACTION_ID)
                .build();
        //@formatter:on

		final var result = testObj.convert(Pair.of(HmcBraintreeTransactionLineStub, HmcMiraklTransactionLineStub));
		assertThat(result.getBraintreeCommerceOrderId()).isEqualTo(BRAINTREE_ORDER_ID);
		assertThat(result.getMiraklOrderId()).isEqualTo(MIRAKL_ORDER_ID);
		assertThat(result.getMiraklSellerId()).isEqualTo(MIRAKL_SELLER_ID);
		assertThat(result.getMiraklTransactionLineId()).isEqualTo(MIRAKL_TRANSACTION_LINE_ID);
		assertThat(result.getMiraklTransactionTime()).isEqualTo(now);
		assertThat(result.getMiraklTransactionType()).isEqualTo(MIRAKL_TRANSACTION_TYPE);
		assertThat(result.getBraintreeAmount()).isEqualTo(BigDecimal.TEN);
		assertThat(result.getMiraklDebitAmount()).isEqualTo(BigDecimal.ZERO);
		assertThat(result.getMiraklCreditAmount()).isEqualTo(BigDecimal.TEN);
		assertThat(result.getBraintreeTransactionId()).isEqualTo(PAYMENT_TRANSACTION_ID);
		assertThat(result.getBraintreeTransactionTime()).isEqualTo(now);
	}

	@Test
	void convert_shouldReturnNullWhenWhenBraintreeTransactionLineIsNull() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();

		//@formatter:off
		final HmcMiraklTransactionLine HmcMiraklTransactionLineStub = HmcMiraklTransactionLine.builder()
				.orderId(MIRAKL_ORDER_ID)
				.sellerId(MIRAKL_SELLER_ID)
				.transactionLineId(MIRAKL_TRANSACTION_LINE_ID)
				.transactionTime(now)
				.transactionType(MIRAKL_TRANSACTION_TYPE)
				.amount(BigDecimal.ONE)
				.debitAmount(BigDecimal.ZERO)
				.creditAmount(BigDecimal.TEN)
				.transactionNumber(PAYMENT_TRANSACTION_ID)
				.build();
        //@formatter:on

		final var result = testObj.convert(Pair.of(null, HmcMiraklTransactionLineStub));
		assertThat(result).isNull();
	}

	@Test
	void convert_shouldReturnNullWhenWhenMiraklTransactionLineIsNull() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();

		//@formatter:off
		final HmcBraintreeTransactionLine HmcBraintreeTransactionLineStub = HmcBraintreeTransactionLine.builder()
				.orderId(BRAINTREE_ORDER_ID)
				.amount(BigDecimal.TEN)
				.currencyIsoCode(EUR_CURRENCY_ISO_CODE)
				.paymentTransactionId(PAYMENT_TRANSACTION_ID)
				.paymentTransactionTime(now)
				.build();
		//@formatter:on

		final var result = testObj.convert(Pair.of(HmcBraintreeTransactionLineStub, null));
		assertThat(result).isNull();
	}

	@Test
	void convert_shouldReturnNullWhenPaymentTransactionIdsAreDifferent() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
		final HmcBraintreeTransactionLine HmcBraintreeTransactionLineStub = HmcBraintreeTransactionLine.builder()
				.orderId(BRAINTREE_ORDER_ID)
				.amount(BigDecimal.TEN)
				.currencyIsoCode(EUR_CURRENCY_ISO_CODE)
				.paymentTransactionId(PAYMENT_TRANSACTION_ID)
				.paymentTransactionTime(now)
				.build();
		//@formatter:on

		//@formatter:off
		final HmcMiraklTransactionLine HmcMiraklTransactionLineStub = HmcMiraklTransactionLine.builder()
				.orderId(MIRAKL_ORDER_ID)
				.sellerId(MIRAKL_SELLER_ID)
				.transactionLineId(MIRAKL_TRANSACTION_LINE_ID)
				.transactionTime(now)
				.transactionType(MIRAKL_TRANSACTION_TYPE)
				.amount(BigDecimal.ONE)
				.debitAmount(BigDecimal.ZERO)
				.creditAmount(BigDecimal.TEN)
				.transactionNumber("paymentTransactionId2")
				.build();
		//@formatter:on

		final var result = testObj.convert(Pair.of(HmcBraintreeTransactionLineStub, HmcMiraklTransactionLineStub));
		assertThat(result).isNull();
	}

}
