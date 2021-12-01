package com.paypal.reports.reportsextract.converter;

import com.paypal.reports.reportsextract.model.HmcFinancialReportLine;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklTransactionLineToFinancialReportLineConverterTest {

	private static final String SELLER_ID = "2000";

	private static final String ORDER_ID = "120000";

	private static final String TRANSACTION_NUMBER = "jhv8jh";

	private static final String ORDER_AMOUNT = "ORDER_AMOUNT";

	private static final String TRANSACTION_LINE_ID = "1234546587";

	private static final BigDecimal CREDIT_AMOUNT = BigDecimal.valueOf(0.00D);

	private static final BigDecimal DEBIT_AMOUNT = BigDecimal.valueOf(20.00D);

	@InjectMocks
	private MiraklTransactionLineToFinancialReportLineConverter testObj;

	@Test
	void convert_shouldReturnHMCFinancialReportTimeWithAttributesPopulatedWhenAFilledHMCMiraklTransactionLineIsPassed() {
		final LocalDateTime now = LocalDateTime.now();

		//@formatter:off
		final HmcMiraklTransactionLine source = HmcMiraklTransactionLine.builder()
				.amount(BigDecimal.valueOf(20.00D))
				.currencyIsoCode("USD")
				.transactionLineId(TRANSACTION_LINE_ID)
				.creditAmount(CREDIT_AMOUNT)
				.debitAmount(DEBIT_AMOUNT)
				.sellerId(SELLER_ID)
				.transactionNumber(TRANSACTION_NUMBER)
				.transactionTime(now)
				.orderId(ORDER_ID)
				.transactionType(ORDER_AMOUNT)
				.build();
		//@formatter:on

		final HmcFinancialReportLine result = testObj.convert(source);

		assertThat(result.getMiraklTransactionLineId()).isEqualTo(TRANSACTION_LINE_ID);
		assertThat(result.getMiraklTransactionTime()).isEqualTo(now);
		assertThat(result.getMiraklTransactionType()).isEqualTo(ORDER_AMOUNT);
		assertThat(result.getMiraklCreditAmount()).isEqualTo(CREDIT_AMOUNT);
		assertThat(result.getMiraklDebitAmount()).isEqualTo(DEBIT_AMOUNT);
		assertThat(result.getMiraklSellerId()).isEqualTo(SELLER_ID);
	}

}
