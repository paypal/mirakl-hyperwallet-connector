package com.paypal.reports.services.converters;

import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.reports.model.HmcBraintreeTransactionLine;
import com.paypal.reports.model.HmcFinancialReportLine;
import com.paypal.reports.model.graphql.braintree.paymentransaction.BraintreeTransactionTypeEnum;
import com.paypal.reports.services.converters.BraintreeTransactionLineToFinancialReportLineConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCBraintreeTransactionLineToHmcFinancialReportLineConverterTest {

	private static final String EUR_CURRENCY_ISO_CODE = "EUR";

	@InjectMocks
	private BraintreeTransactionLineToFinancialReportLineConverter testObj;

	private static final String ORDER_ID = "orderId";

	private static final String PAYMENT_TRANSACTION_ID = "payamentTransactionId";

	@Test
	void convert_shouldConvertBraintreeTransactionLineIntoFinancialReportLineWhenAllAttributesAreFilled() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
		final HmcBraintreeTransactionLine HmcBraintreeTransactionLineStub = HmcBraintreeTransactionLine.builder()
				.orderId(ORDER_ID)
				.amount(BigDecimal.TEN)
				.currencyIsoCode(EUR_CURRENCY_ISO_CODE)
				.paymentTransactionId(PAYMENT_TRANSACTION_ID)
				.transactionType(BraintreeTransactionTypeEnum.OPERATOR_ORDER_AMOUNT.name())
				.paymentTransactionTime(now)
				.build();
		//@formatter:on

		final HmcFinancialReportLine result = testObj.convert(HmcBraintreeTransactionLineStub);
		assertThat(result.getBraintreeCommerceOrderId()).isEqualTo(ORDER_ID);
		assertThat(result.getCurrencyIsoCode()).isEqualTo(EUR_CURRENCY_ISO_CODE);
		assertThat(result.getBraintreeAmount()).isEqualTo(BigDecimal.TEN);
		assertThat(result.getBraintreeTransactionId()).isEqualTo(PAYMENT_TRANSACTION_ID);
		assertThat(result.getBraintreeTransactionTime()).isEqualTo(now);
		assertThat(result.getMiraklTransactionType())
				.isEqualTo(BraintreeTransactionTypeEnum.OPERATOR_ORDER_AMOUNT.name());
	}

	@Test
	void convert_shouldReturnNullWhenBraintreeTransactionLineIsNull() {
		final HmcFinancialReportLine result = testObj.convert(null);

		assertThat(result).isNull();
	}

}
