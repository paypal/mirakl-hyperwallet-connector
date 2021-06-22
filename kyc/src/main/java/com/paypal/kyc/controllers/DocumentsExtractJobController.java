package com.paypal.kyc.controllers;

import com.paypal.infrastructure.controllers.AbstractJobController;
import com.paypal.kyc.jobs.DocumentsExtractJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Controller that calls manually to all logic responsible of extracting documents from
 * Mirakl and sending them to hyperwallet
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class DocumentsExtractJobController extends AbstractJobController {

	private static final String DEFAULT_DOCUMENTS_EXTRACT_JOB_NAME = "DocumentsExtractJobSingleExecution";

	/**
	 * Triggers the {@link DocumentsExtractJob} with the {@code delta} time to retrieve
	 * shops created or updated since that {@code delta} and schedules the job with the
	 * {@code name} provided
	 * @param delta the {@link Date} in {@link DateTimeFormat.ISO}
	 * @param name the job name in {@link String}
	 * @return a {@link ResponseEntity <String>} with the name of the job scheduled
	 * @throws SchedulerException if quartz {@link org.quartz.Scheduler} fails
	 */
	@PostMapping("/documents-extract")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date delta,
			@RequestParam(required = false, defaultValue = DEFAULT_DOCUMENTS_EXTRACT_JOB_NAME) final String name)
			throws SchedulerException {
		runSingleJob(name, DocumentsExtractJob.class, delta);

		return ResponseEntity.accepted().body(name);
	}

}
