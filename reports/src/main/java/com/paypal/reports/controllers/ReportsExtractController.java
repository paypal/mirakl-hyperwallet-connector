package com.paypal.reports.controllers;

import com.paypal.infrastructure.service.JobService;
import com.paypal.reports.jobs.ReportsExtractJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/job/reports")
public class ReportsExtractController {

	private static final String DEFAULT_FINANCIAL_REPORT_EXTRACT_JOB_NAME = "FinancialReportExtractJobSingleExecution";

	@Resource
	private JobService jobService;

	@PostMapping("/financial-report-extract")
	public ResponseEntity<String> runJob(
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date startDate,
			@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date endDate,
			@RequestParam(required = false, defaultValue = DEFAULT_FINANCIAL_REPORT_EXTRACT_JOB_NAME) final String name,
			@RequestParam(required = false) final String fileName) throws SchedulerException {
		jobService.createAndRunSingleExecutionJob(name, ReportsExtractJob.class,
				ReportsExtractJob.createJobDataMap(startDate, endDate, fileName), null);

		return ResponseEntity.accepted().body(name);
	}

}
