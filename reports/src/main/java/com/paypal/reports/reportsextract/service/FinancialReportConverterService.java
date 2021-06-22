package com.paypal.reports.reportsextract.service;

import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;
import com.paypal.reports.reportsextract.model.HmcFinancialReportLine;
import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;

/**
 * Interface that manages all converters needed to generate the financial report
 */

public interface FinancialReportConverterService {

	/**
	 * Converts a braintree transaction line into a financial report line
	 * @param hmcBraintreeTransactionLine {@link HmcBraintreeTransactionLine}
	 * @return {@link HmcFinancialReportLine}
	 */
	HmcFinancialReportLine convertBraintreeTransactionLineIntoFinancialReportLine(
			HmcBraintreeTransactionLine hmcBraintreeTransactionLine);

	/**
	 * Converts a mirakl transaction line into a financial report line
	 * @param hmcMiraklTransactionLine {@link HmcMiraklTransactionLine}
	 * @return {@link HmcFinancialReportLine}
	 */
	HmcFinancialReportLine convertMiraklTransactionLineIntoFinancialReportLine(
			HmcMiraklTransactionLine hmcMiraklTransactionLine);

	/**
	 * Converts a pair of {@link HmcBraintreeTransactionLine} and
	 * {@link HmcMiraklTransactionLine} into a financial report line
	 * @param hmcBraintreeTransactionLine {@link HmcBraintreeTransactionLine}
	 * @param hmcMiraklTransactionLine {@link HmcMiraklTransactionLine}
	 * @return {@link HmcFinancialReportLine}
	 */
	HmcFinancialReportLine convertBrainTreeAndMiraklTransactionLineIntoFinancialReportLine(
			HmcBraintreeTransactionLine hmcBraintreeTransactionLine, HmcMiraklTransactionLine hmcMiraklTransactionLine);

}
