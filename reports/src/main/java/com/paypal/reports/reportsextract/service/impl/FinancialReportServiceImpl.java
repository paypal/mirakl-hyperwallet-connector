package com.paypal.reports.reportsextract.service.impl;

import com.paypal.reports.infraestructure.configuration.ReportsConfig;
import com.paypal.reports.reportsextract.model.HmcBraintreeRefundLine;
import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.HmcFinancialReportLine;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;
import com.paypal.reports.reportsextract.model.graphql.braintree.paymentransaction.BraintreeTransactionStatusEnum;
import com.paypal.reports.reportsextract.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link FinancialReportService}
 */
@Service
public class FinancialReportServiceImpl implements FinancialReportService {

	private final FinancialReportConverterService financialReportConverterService;

	private final ReportsConfig reportsConfig;

	private final HMCFileService hmcFileService;

	private final ReportsBraintreeTransactionsExtractService reportsBraintreeTransactionsExtractService;

	private final ReportsBraintreeRefundsExtractService reportsBraintreeRefundsExtractService;

	private final ReportsMiraklExtractService reportsMiraklExtractService;

	public FinancialReportServiceImpl(final FinancialReportConverterService financialReportConverterService,
			final ReportsConfig reportsConfig, final HMCFileService hmcFileService,
			final ReportsBraintreeTransactionsExtractService reportsBraintreeTransactionsExtractService,
			final ReportsBraintreeRefundsExtractService reportsBraintreeRefundsExtractService,
			final ReportsMiraklExtractService reportsMiraklExtractService) {
		this.financialReportConverterService = financialReportConverterService;
		this.reportsConfig = reportsConfig;
		this.hmcFileService = hmcFileService;
		this.reportsBraintreeTransactionsExtractService = reportsBraintreeTransactionsExtractService;
		this.reportsBraintreeRefundsExtractService = reportsBraintreeRefundsExtractService;
		this.reportsMiraklExtractService = reportsMiraklExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateFinancialReport(final Date startDate, final Date endDate, final String prefixFileName) {

		final List<HmcBraintreeTransactionLine> braintreeTransactionsAndRefunds = getBraintreeTransactionsAndRefunds(
				startDate, endDate);
		final List<HmcMiraklTransactionLine> miraklTransactions = getMiraklTransactions(startDate, endDate);

		final Map<String, List<HmcBraintreeTransactionLine>> braintreeTransactionLines = groupBraintreeTransactionLinesByPaymentTransactionId(
				braintreeTransactionsAndRefunds);

		final Map<String, List<HmcMiraklTransactionLine>> miraklCommonTransactionLines = groupMiraklTransactionLinesByTransactionNumber(
				miraklTransactions);

		final List<HmcMiraklTransactionLine> miraklTransactionLinesWithEmptyTransactionNumber = groupMiraklTransactionLinesByWithEmptyTransactionNumber(
				miraklTransactions);

		final List<HmcBraintreeTransactionLine> uncommonHmcBraintreeTransactionLines = getBraintreeNotCommonTransactionLines(
				braintreeTransactionLines, miraklCommonTransactionLines);

		final Map<HmcBraintreeTransactionLine, List<HmcMiraklTransactionLine>> commonTransactions = groupMiraklTransactionLinesByBraintreeTransactionLines(
				braintreeTransactionLines, miraklCommonTransactionLines);

		final List<HmcMiraklTransactionLine> uncommonMiraklTransactionLines = getNotCommonMiraklTransactions(
				braintreeTransactionLines, miraklCommonTransactionLines);

		final List<HmcFinancialReportLine> financialReportLines = new ArrayList<>();

		financialReportLines.addAll(convertUncommonBraintreeTransactionLines(uncommonHmcBraintreeTransactionLines));
		financialReportLines
				.addAll(convertUncommonMiraklTransactionLines(miraklTransactionLinesWithEmptyTransactionNumber));
		financialReportLines.addAll(convertCommonTransactionLines(commonTransactions));
		financialReportLines.addAll(convertUncommonMiraklTransactionLines(uncommonMiraklTransactionLines));

		//@formatter:off
		final List<String> financialReportStringLines = financialReportLines.stream()
				.map(HmcFinancialReportLine::toString)
				.collect(Collectors.toList());
		//@formatter:on

		final String fileName;

		if (StringUtils.isNotEmpty(prefixFileName)) {
			fileName = hmcFileService.saveCSVFile(reportsConfig.getRepoPath(), prefixFileName,
					financialReportStringLines, reportsConfig.getFinancialReportHeader());
		}
		else {
			fileName = hmcFileService.saveCSVFile(reportsConfig.getRepoPath(),
					reportsConfig.getFinancialReportPrefixFileName(), financialReportStringLines,
					reportsConfig.getFinancialReportHeader());
		}

		return fileName;
	}

	private List<HmcMiraklTransactionLine> getNotCommonMiraklTransactions(
			final Map<String, List<HmcBraintreeTransactionLine>> braintreeTransactionLines,
			final Map<String, List<HmcMiraklTransactionLine>> miraklCommonTransactionLines) {
		//@formatter:off
		return miraklCommonTransactionLines.keySet().stream()
				.filter(Predicate.not(braintreeTransactionLines::containsKey))
				.map(miraklCommonTransactionLines::get)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@NonNull
	private Map<HmcBraintreeTransactionLine, List<HmcMiraklTransactionLine>> groupMiraklTransactionLinesByBraintreeTransactionLines(
			final Map<String, List<HmcBraintreeTransactionLine>> braintreeTransactionLines,
			final Map<String, List<HmcMiraklTransactionLine>> miraklCommonTransactionLines) {
		//@formatter:off
		return braintreeTransactionLines.keySet().stream()
				.filter(miraklCommonTransactionLines::containsKey)
				.map(braintreeTransactionLines::get)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(braintreeTransactionLine -> braintreeTransactionLine,
						braintreeTransactionLine -> miraklCommonTransactionLines
								.get(braintreeTransactionLine.getPaymentTransactionId())));
		//@formatter:on
	}

	@NonNull
	private List<HmcBraintreeTransactionLine> getBraintreeNotCommonTransactionLines(
			final Map<String, List<HmcBraintreeTransactionLine>> braintreeTransactionLines,
			final Map<String, List<HmcMiraklTransactionLine>> miraklCommonTransactionLines) {
		//@formatter:off
		return braintreeTransactionLines.keySet().stream()
				.filter(Predicate.not(miraklCommonTransactionLines::containsKey))
				.map(braintreeTransactionLines::get)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@NonNull
	private List<HmcMiraklTransactionLine> groupMiraklTransactionLinesByWithEmptyTransactionNumber(
			final List<HmcMiraklTransactionLine> miraklTransactions) {
		//@formatter:off

		return Stream.ofNullable(miraklTransactions)
				.flatMap(Collection::stream)
				.filter(miraklTransaction -> StringUtils.isEmpty(miraklTransaction.getTransactionNumber()))
				.collect(Collectors.toList());
		//@formatter:on
	}

	private Map<String, List<HmcMiraklTransactionLine>> groupMiraklTransactionLinesByTransactionNumber(
			final List<HmcMiraklTransactionLine> miraklTransactions) {
		//@formatter:off
		return Stream.ofNullable(miraklTransactions)
				.flatMap(Collection::stream)
				.filter(miraklTransaction -> StringUtils.isNotEmpty(miraklTransaction.getTransactionNumber()))
				.collect(Collectors.groupingBy(HmcMiraklTransactionLine::getTransactionNumber));
		//@formatter:on
	}

	private Map<String, List<HmcBraintreeTransactionLine>> groupBraintreeTransactionLinesByPaymentTransactionId(
			final List<HmcBraintreeTransactionLine> braintreeTransactions) {
		//@formatter:off
		return Stream
				.ofNullable(braintreeTransactions)
				.flatMap(Collection::stream)
				.collect(Collectors.groupingBy(HmcBraintreeTransactionLine::getPaymentTransactionId));
		//@formatter:on
	}

	private List<HmcMiraklTransactionLine> getMiraklTransactions(final Date startDate, final Date endDate) {
		return reportsMiraklExtractService.getAllTransactionLinesByDate(startDate, endDate);
	}

	private List<HmcBraintreeTransactionLine> getBraintreeTransactionsAndRefunds(final Date startDate,
			final Date endDate) {
		final List<HmcBraintreeTransactionLine> braintreeTransactionsAndRefunds = new ArrayList<>();

		final List<HmcBraintreeTransactionLine> braintreeTransactions = reportsBraintreeTransactionsExtractService
				.getAllTransactionsByTypeAndDateInterval(BraintreeTransactionStatusEnum.SETTLED.toString(), startDate,
						endDate);

		final List<HmcBraintreeRefundLine> braintreeRefunds = reportsBraintreeRefundsExtractService
				.getAllRefundsByTypeAndDateInterval(BraintreeTransactionStatusEnum.SETTLED.toString(), startDate,
						endDate);

		braintreeTransactionsAndRefunds.addAll(braintreeTransactions);
		braintreeTransactionsAndRefunds.addAll(braintreeRefunds);

		return braintreeTransactionsAndRefunds;
	}

	private List<HmcFinancialReportLine> generateFinancialReportLine(
			final HmcBraintreeTransactionLine braintreeTransactionLine,
			final List<HmcMiraklTransactionLine> miraklTransactionLines) {
		return Optional.ofNullable(miraklTransactionLines).orElse(List.of()).stream()
				.map(miraklTransactionLine -> financialReportConverterService
						.convertBrainTreeAndMiraklTransactionLineIntoFinancialReportLine(braintreeTransactionLine,
								miraklTransactionLine))
				.collect(Collectors.toList());
	}

	private List<HmcFinancialReportLine> convertCommonTransactionLines(
			final Map<HmcBraintreeTransactionLine, List<HmcMiraklTransactionLine>> commonTransactions) {
		return commonTransactions.entrySet().stream()
				.map(entry -> generateFinancialReportLine(entry.getKey(), entry.getValue())).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private List<HmcFinancialReportLine> convertUncommonBraintreeTransactionLines(
			final List<HmcBraintreeTransactionLine> uncommonHmcBraintreeTransactionLines) {
		return uncommonHmcBraintreeTransactionLines.stream()
				.map(financialReportConverterService::convertBraintreeTransactionLineIntoFinancialReportLine)
				.collect(Collectors.toList());
	}

	private List<HmcFinancialReportLine> convertUncommonMiraklTransactionLines(
			final List<HmcMiraklTransactionLine> uncommonHmcMiraklTransactionLines) {
		return uncommonHmcMiraklTransactionLines.stream()
				.map(financialReportConverterService::convertMiraklTransactionLineIntoFinancialReportLine)
				.collect(Collectors.toList());
	}

}
