package com.paypal.reports.reportsextract.service;

import com.paypal.reports.reportsextract.model.HmcMiraklTransactionLine;

import java.util.Date;
import java.util.List;

public interface ReportsMiraklExtractService {

	List<HmcMiraklTransactionLine> getAllTransactionLinesByDate(Date startDate, Date endDate);

}
