package com.paypal.reports.services;

import com.paypal.reports.model.HmcMiraklTransactionLine;

import java.util.Date;
import java.util.List;

public interface ReportsMiraklExtractService {

	List<HmcMiraklTransactionLine> getAllTransactionLinesByDate(Date startDate, Date endDate);

}
