package com.paypal.reports.services;

import com.paypal.reports.model.HmcBraintreeRefundLine;

import java.util.Date;
import java.util.List;

public interface ReportsBraintreeRefundsExtractService {

	/**
	 * Retrieves all the refunds registered in Braintree by {@code transactionType},
	 * {@code startDate} and {@code endDate}
	 * @param transactionType e.g. SETTLED
	 * @param startDate Starting date
	 * @param endDate Ending date
	 * @return a filtered {@link List < HmcBraintreeTransactionLine >}
	 */
	List<HmcBraintreeRefundLine> getAllRefundsByTypeAndDateInterval(String transactionType, Date startDate,
			Date endDate);

}
