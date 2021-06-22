package com.paypal.reports.reportsextract.service;

import java.util.Date;

/**
 * Interface that generates a financial report
 */
public interface FinancialReportService {

	/**
	 * Generates a Financial Report from startDate to endDate, using a prefix name for the
	 * file created
	 * @param startDate {@link Date} start date
	 * @param endDate {@link Date} end date
	 * @param fileName {@link String} prefix file name
	 * @return {@link String} file name generated in hard disk
	 */
	String generateFinancialReport(Date startDate, Date endDate, String fileName);

}
