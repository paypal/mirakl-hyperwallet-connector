package com.paypal.reports.reportsextract.service.impl;

import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import com.paypal.reports.infraestructure.configuration.ReportsConfig;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.HmcFinancialReportLine;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction.BraintreeTransactionStatusEnum;
import com.paypal.reports.reportsextract.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialReportServiceImplTest {

	private FinancialReportServiceImpl testObj;

	@Mock
	private FinancialReportConverterService financialReportConverterServiceMock;

	@Mock
	private ReportsConfig reportsConfigMock;

	@Mock
	private HMCFileService hmcFileServiceMock;

	@Mock
	private ReportsBraintreeTransactionsExtractService reportsBraintreeTransactionsExtractServiceMock;

	@Mock
	private ReportsBraintreeRefundsExtractService reportsBraintreeRefundsExtractServiceMock;

	@Mock
	private ReportsMiraklExtractService reportsMiraklExtractServiceMock;

	private static final String PREFIX_FILE_NAME = "prefixFileName";

	private static final String FILE_NAME = "fileName";

	private static final String REPO_PATH = "/repo/path";

	private static final String HEADER = "braintreeCommerceOrderId,miraklOrderId,miraklSellerId,miraklTransactionLineId,miraklTransactionTime,TransactionType,braintreeAmount,miraklDebitAmount,miraklCreditAmount,currencyIsoCode,braintreeTransactionId,braintreeTransactionTime";

	private static final List<String> FINANCIAL_REPORT_LINES = List.of(
			"20001,,,,,,120,,,EUR,6jkals83,2020-11-10 20:45:00",
			",000000009-B,2003,abcd-12345-2145-bb,2020-11-10 20:45:00,ORDER_CANCELATION,0,99.0,,GBP,7jkals83,",
			"20000,000000009-A,2002,abcd-12345-2145-aa,2020-11-10 20:45:00,ORDER_AMOUNT,100,93.0,,USD,5jkals83,2020-11-10 20:45:00");

	@BeforeEach
	void setUp() {
		testObj = new FinancialReportServiceImpl(financialReportConverterServiceMock, reportsConfigMock,
				hmcFileServiceMock, reportsBraintreeTransactionsExtractServiceMock,
				reportsBraintreeRefundsExtractServiceMock, reportsMiraklExtractServiceMock);
	}

	@Test
	void generateFinancialReport_shouldCallBrainTreeAndMiraklAndGenerateProperFinancialReportLines() {
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 11, 10, 20, 45));
		final LocalDateTime startLocalDate = TimeMachine.now();
		TimeMachine.useFixedClockAt(LocalDateTime.of(2020, 12, 10, 20, 45));
		final LocalDateTime endLocalDate = TimeMachine.now();

		final Date startDate = DateUtil.convertToDate(startLocalDate, ZoneId.systemDefault());
		final Date endDate = DateUtil.convertToDate(endLocalDate, ZoneId.systemDefault());

		final HmcBraintreeTransactionLine braintreeCommonTransactionLine = getBraintreeCommonTransactionLine(
				startLocalDate);
		final HmcBraintreeTransactionLine unjoinableBraintreeTransactionLine = getUnjoinableBraintreeTransactionLine(
				startLocalDate);
		final HmcMiraklTransactionLine unjoinableMiraklTransactionLine = getUnjoinableMiraklTransactionLine(
				startLocalDate);
		final HmcMiraklTransactionLine miraklCommonTransactionLine = getMiraklCommonTransactionLine(startLocalDate);
		final HmcFinancialReportLine unjoinableBraintreeFinancialLine = getUnjoinableBraintreeFinancialLine(
				startLocalDate);
		final HmcFinancialReportLine unjoinableMiraklFinancialLine = getUnjoinableMiraklFinancialLine(startLocalDate);
		final HmcFinancialReportLine commonBraintreeAndMiraklFinancialLine = getCommonBraintreeAndMiraklFinancialLine(
				startLocalDate);

		when(reportsConfigMock.getRepoPath()).thenReturn(REPO_PATH);
		when(reportsConfigMock.getFinancialReportHeader()).thenReturn(HEADER);

		when(reportsBraintreeTransactionsExtractServiceMock.getAllTransactionsByTypeAndDateInterval(
				BraintreeTransactionStatusEnum.SETTLED.toString(), startDate, endDate))
						.thenReturn(List.of(braintreeCommonTransactionLine, unjoinableBraintreeTransactionLine));

		when(reportsMiraklExtractServiceMock.getAllTransactionLinesByDate(startDate, endDate))
				.thenReturn(List.of(unjoinableMiraklTransactionLine, miraklCommonTransactionLine));

		when(financialReportConverterServiceMock
				.convertBraintreeTransactionLineIntoFinancialReportLine(unjoinableBraintreeTransactionLine))
						.thenReturn(unjoinableBraintreeFinancialLine);
		when(financialReportConverterServiceMock
				.convertMiraklTransactionLineIntoFinancialReportLine(unjoinableMiraklTransactionLine))
						.thenReturn(unjoinableMiraklFinancialLine);
		when(financialReportConverterServiceMock.convertBrainTreeAndMiraklTransactionLineIntoFinancialReportLine(
				braintreeCommonTransactionLine, miraklCommonTransactionLine))
						.thenReturn(commonBraintreeAndMiraklFinancialLine);

		when(hmcFileServiceMock.saveCSVFile(REPO_PATH, PREFIX_FILE_NAME, FINANCIAL_REPORT_LINES, HEADER))
				.thenReturn(FILE_NAME);

		testObj.generateFinancialReport(startDate, endDate, PREFIX_FILE_NAME);

		verify(hmcFileServiceMock).saveCSVFile(REPO_PATH, PREFIX_FILE_NAME, FINANCIAL_REPORT_LINES, HEADER);
	}

	private HmcBraintreeTransactionLine getUnjoinableBraintreeTransactionLine(final LocalDateTime date) {
		//@formatter:off
		return HmcBraintreeTransactionLine.builder()
				.orderId("20001")
				.amount(BigDecimal.valueOf(120))
				.currencyIsoCode("EUR")
				.paymentTransactionTime(date)
				.paymentTransactionId("6jkals83")
				.build();
		//@formatter:on
	}

	private HmcFinancialReportLine getUnjoinableBraintreeFinancialLine(final LocalDateTime date) {
		//@formatter:off
		return HmcFinancialReportLine.builder()
				.braintreeCommerceOrderId("20001")
				.braintreeAmount(BigDecimal.valueOf(120))
				.currencyIsoCode("EUR")
				.braintreeTransactionTime(date)
				.braintreeTransactionId("6jkals83")
				.build();
		//@formatter:on
	}

	private HmcBraintreeTransactionLine getBraintreeCommonTransactionLine(final LocalDateTime date) {
		//@formatter:off
		return HmcBraintreeTransactionLine.builder()
				.orderId("20000")
				.amount(BigDecimal.valueOf(100))
				.currencyIsoCode("USD")
				.paymentTransactionTime(date)
				.paymentTransactionId("5jkals83")
				.build();
		//@formatter:on
	}

	private HmcMiraklTransactionLine getUnjoinableMiraklTransactionLine(final LocalDateTime date) {
		//@formatter:off
		return HmcMiraklTransactionLine.builder()
				.orderId("000000009-B")
				.sellerId("2003")
				.transactionLineId("abcd-12345-2145-bb")
				.transactionType("ORDER_CANCELATION")
				.transactionTime(date)
				.debitAmount(BigDecimal.valueOf(99.0))
				.currencyIsoCode("GBP")
				.build();
		//@formatter:on
	}

	private HmcFinancialReportLine getUnjoinableMiraklFinancialLine(final LocalDateTime date) {
		//@formatter:off
		return HmcFinancialReportLine.builder()
				.miraklOrderId("000000009-B")
				.miraklSellerId("2003")
				.miraklTransactionLineId("abcd-12345-2145-bb")
				.miraklTransactionType("ORDER_CANCELATION")
				.miraklTransactionTime(date)
				.miraklDebitAmount(BigDecimal.valueOf(99.0))
				.currencyIsoCode("GBP")
				.braintreeTransactionId("7jkals83")
				.build();
		//@formatter:on
	}

	private HmcFinancialReportLine getCommonBraintreeAndMiraklFinancialLine(final LocalDateTime date) {
		//@formatter:off
		return HmcFinancialReportLine.builder()
				.braintreeCommerceOrderId("20000")
				.miraklOrderId("000000009-A")
				.miraklSellerId("2002")
				.miraklTransactionLineId("abcd-12345-2145-aa")
				.miraklTransactionType("ORDER_AMOUNT")
				.miraklTransactionTime(date)
				.braintreeAmount(BigDecimal.valueOf(100))
				.miraklDebitAmount(BigDecimal.valueOf(93.0))
				.currencyIsoCode("USD")
				.braintreeTransactionId("5jkals83")
				.braintreeTransactionTime(date)
				.build();
		//@formatter:on
	}

	private HmcMiraklTransactionLine getMiraklCommonTransactionLine(final LocalDateTime date) {
		//@formatter:off
		return HmcMiraklTransactionLine.builder()
				.orderId("000000009-A")
				.sellerId("2002")
				.transactionLineId("abcd-12345-2145-aa")
				.transactionType("ORDER_AMOUNT")
				.transactionTime(date)
				.debitAmount(BigDecimal.valueOf(93.0))
				.currencyIsoCode("USD")
				.transactionNumber("5jkals83")
				.build();
		//@formatter:on
	}

}
