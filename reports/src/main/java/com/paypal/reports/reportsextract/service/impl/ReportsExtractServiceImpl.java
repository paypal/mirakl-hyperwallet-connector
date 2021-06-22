package com.paypal.reports.reportsextract.service.impl;

import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.reports.infraestructure.configuration.ReportsConfig;
import com.paypal.reports.reportsextract.service.FinancialReportService;
import com.paypal.reports.reportsextract.service.ReportsExtractService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReportsExtractServiceImpl implements ReportsExtractService {

	private final FinancialReportService financialReportService;

	private final MailNotificationUtil mailNotificationUtil;

	private final ReportsConfig reportsConfig;

	public ReportsExtractServiceImpl(final FinancialReportService financialReportService,
			final MailNotificationUtil mailNotificationUtil, final ReportsConfig reportsConfig) {
		this.financialReportService = financialReportService;
		this.mailNotificationUtil = mailNotificationUtil;
		this.reportsConfig = reportsConfig;
	}

	@Override
	public void extractFinancialReport(final Date startDate, final Date endDate, final String fileName) {
		final String name = financialReportService.generateFinancialReport(startDate, endDate, fileName);
		mailNotificationUtil.sendPlainTextEmail(
				"Your Marketplace Financial Report from " + startDate + " to " + endDate,
				"Below there is a link to your Marketplace Financial Report for the period from " + startDate + " to "
						+ endDate + ". Please click on it to download the report:\n\n" + reportsConfig.getHmcServerUri()
						+ "/downloads/financial-report/" + name);
	}

}
