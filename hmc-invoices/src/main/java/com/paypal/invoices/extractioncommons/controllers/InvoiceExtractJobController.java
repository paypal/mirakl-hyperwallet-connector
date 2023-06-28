package com.paypal.invoices.extractioncommons.controllers;

import com.paypal.invoices.extractioncommons.jobs.InvoicesExtractJob;
import com.paypal.jobsystem.quartzintegration.controllers.AbstractJobController;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Specific controller to fire Extract Invoices process
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class InvoiceExtractJobController extends AbstractJobController {

	private static final String DEFAULT_INVOICES_EXTRACT_JOB_NAME = "InvoicesExtractJobSingleExecution";

	public static final String INCLUDE_PAID = "includePaid";

	/**
	 * Triggers the {@link InvoicesExtractJob} with the {@code delta} time to retrieve
	 * invoices created or updated since that {@code delta} and schedules the job with the
	 * {@code name} provided
	 * @param delta the {@link Date} in {@link DateTimeFormat.ISO}
	 * @param name the job name in {@link String}
	 * @param includePaid includes into the extraction paid invoices {@link Boolean}
	 * @return a {@link ResponseEntity <String>} with the name of the job scheduled
	 * @throws SchedulerException if quartz {@link org.quartz.Scheduler} fails
	 */
	@PostMapping("/invoices-extract")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date delta,
			@RequestParam(required = false, defaultValue = DEFAULT_INVOICES_EXTRACT_JOB_NAME) final String name,
			@RequestParam(required = false) final Boolean includePaid) throws SchedulerException {
		runSingleJob(name, InvoicesExtractJob.class, delta,
				includePaid != null ? Map.of(INCLUDE_PAID, includePaid) : Map.of());

		return ResponseEntity.accepted().body(name);
	}

}
