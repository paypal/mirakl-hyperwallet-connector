package com.paypal.sellers.controllers;

import com.paypal.infrastructure.controllers.AbstractJobController;
import com.paypal.sellers.jobs.IndividualSellersExtractJob;
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
 * Specific controller to fire Extract Seller process
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class IndividualSellersExtractJobController extends AbstractJobController {

	private static final String DEFAULT_SELLERS_EXTRACT_JOB_NAME = "SellersExtractJobSingleExecution";

	/**
	 * Triggers the {@link IndividualSellersExtractJob} with the {@code delta} time to
	 * retrieve shops created or updated since that {@code delta} and schedules the job
	 * with the {@code name} provided
	 * @param delta the {@link Date} in {@link DateTimeFormat.ISO}
	 * @param name the job name in {@link String}
	 * @return a {@link ResponseEntity<String>} with the name of the job scheduled
	 * @throws SchedulerException if quartz {@link org.quartz.Scheduler} fails
	 */
	@PostMapping("/sellers-extract")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date delta,
			@RequestParam(required = false, defaultValue = DEFAULT_SELLERS_EXTRACT_JOB_NAME) final String name)
			throws SchedulerException {

		runSingleJob(name, IndividualSellersExtractJob.class, delta);

		return ResponseEntity.accepted().body(name);
	}

}
