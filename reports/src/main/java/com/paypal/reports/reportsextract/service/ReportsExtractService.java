package com.paypal.reports.reportsextract.service;

import java.util.Date;

public interface ReportsExtractService {

	void extractFinancialReport(Date startDate, Date endDate, String fileName);

}
