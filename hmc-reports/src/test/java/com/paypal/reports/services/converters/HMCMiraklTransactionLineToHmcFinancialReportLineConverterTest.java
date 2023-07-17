package com.paypal.reports.services.converters;

import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.reports.model.HmcFinancialReportLine;
import com.paypal.reports.model.HmcMiraklTransactionLine;
import com.paypal.reports.services.converters.MiraklTransactionLineToFinancialReportLineConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCMiraklTransactionLineToHmcFinancialReportLineConverterTest {

	private static final String ORDER_ID = "orderId";

	private static final String SELLER_ID = "sellerId";

	private static final String TRANSACTION_TYPE = "transactionType";

	private static final String PAYMENT_TRANSACTION_ID = "payamentTransactionId";

	private static final String MIRAKL_TRANSACTION_LINE_ID = "miraklTransactionLineId";

	@InjectMocks
	private MiraklTransactionLineToFinancialReportLineConverter testObj;

	@Test
	void convert_shouldConvertMiraklTransactionLineIntoFinancialReportLineWhenAllAttributesAreFilled() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime now = TimeMachine.now();
		//@formatter:off
		final HmcMiraklTransactionLine HmcMiraklTransactionLineStub = HmcMiraklTransactionLine.builder()
				.orderId(ORDER_ID)
				.sellerId(SELLER_ID)
				.transactionLineId(MIRAKL_TRANSACTION_LINE_ID)
				.transactionTime(now)
				.transactionType(TRANSACTION_TYPE)
				.amount(BigDecimal.ONE)
				.creditAmount(BigDecimal.TEN)
				.debitAmount(BigDecimal.ZERO)
				.transactionNumber(PAYMENT_TRANSACTION_ID)
				.build();
		//@formatter:on

		final HmcFinancialReportLine result = testObj.convert(HmcMiraklTransactionLineStub);
		assertThat(result.getMiraklOrderId()).isEqualTo(ORDER_ID);
		assertThat(result.getMiraklSellerId()).isEqualTo(SELLER_ID);
		assertThat(result.getMiraklTransactionLineId()).isEqualTo(MIRAKL_TRANSACTION_LINE_ID);
		assertThat(result.getMiraklTransactionTime()).isEqualTo(now);
		assertThat(result.getMiraklTransactionType()).isEqualTo(TRANSACTION_TYPE);
		assertThat(result.getMiraklCreditAmount()).isEqualTo(BigDecimal.TEN);
		assertThat(result.getMiraklDebitAmount()).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	void convert_shouldReturnNullWhenMiraklTransactionLineIsNull() {
		final HmcFinancialReportLine result = testObj.convert(null);

		assertThat(result).isNull();
	}

}
