package com.paypal.reports.jobs;

import com.paypal.reports.services.ReportsExtractService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Date;

/**
 * Extract invoices job for extracting Mirakl invoices data and populate it on HyperWallet
 * as users
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReportsExtractJob implements Job {

	private static final String START_DATE = "startDate";

	private static final String END_DATE = "endDate";

	private static final String FILE_NAME = "fileName";

	@Resource
	protected ReportsExtractService reportsExtractService;

	/**
	 * Creates the delta time for mirakl data retrieval query
	 * @param startDate the start {@link Date} interval
	 * @param endDate the end {@link Date} interval
	 * @return the {@link JobDataMap}
	 */
	public static JobDataMap createJobDataMap(final Date startDate, final Date endDate, final String fileName) {
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(START_DATE, startDate);
		jobDataMap.put(END_DATE, endDate);
		jobDataMap.put(FILE_NAME, fileName);
		return jobDataMap;
	}

	@Override
	public void execute(final JobExecutionContext context) {
		reportsExtractService.extractFinancialReport(getStartDate(context), getEndDate(context), getFileName(context));
	}

	/**
	 * Retrieves the start date from {@link JobDataMap} linked to
	 * {@link JobExecutionContext}
	 * @param context the {@link JobExecutionContext}
	 * @return the {@link Date}
	 */
	public Date getStartDate(final JobExecutionContext context) {
		return (Date) context.getJobDetail().getJobDataMap().get(START_DATE);
	}

	/**
	 * Retrieves the end date from {@link JobDataMap} linked to
	 * {@link JobExecutionContext}
	 * @param context the {@link JobExecutionContext}
	 * @return the {@link Date}
	 */
	public Date getEndDate(final JobExecutionContext context) {
		return (Date) context.getJobDetail().getJobDataMap().get(END_DATE);
	}

	/**
	 * Retrieves the fileName from {@link JobDataMap} linked to
	 * {@link JobExecutionContext}
	 * @param context the {@link JobExecutionContext}
	 * @return the {@link Date}
	 */
	public String getFileName(final JobExecutionContext context) {
		return (String) context.getJobDetail().getJobDataMap().get(FILE_NAME);
	}

}
