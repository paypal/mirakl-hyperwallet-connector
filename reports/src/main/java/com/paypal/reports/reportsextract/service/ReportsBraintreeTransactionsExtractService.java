package com.paypal.reports.reportsextract.service;

import com.paypal.reports.reportsextract.model.HmcBraintreeTransactionLine;

import java.util.Date;
import java.util.List;

/**
 * Service that retrieves data from Braintree API focused on the financial report
 * generation
 */
public interface ReportsBraintreeTransactionsExtractService {

	/**
	 * Retrieves all the transactions registered in Braintree by {@code transactionType},
	 * {@code startDate} and {@code endDate}
	 * @param transactionType e.g. SETTLED
	 * @param startDate Starting date
	 * @param endDate Ending date
	 * @return a filtered {@link List< HmcBraintreeTransactionLine >}
	 */
	List<HmcBraintreeTransactionLine> getAllTransactionsByTypeAndDateInterval(String transactionType, Date startDate,
			Date endDate);

}
