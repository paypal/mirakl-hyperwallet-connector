package com.paypal.reports.services;

import java.util.Date;

public interface ReportsExtractService {

	void extractFinancialReport(Date startDate, Date endDate, String fileName);

}
