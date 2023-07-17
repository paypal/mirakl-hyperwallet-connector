package com.paypal.kyc.statussynchronization.controllers;

import com.paypal.jobsystem.quartzintegration.controllers.AbstractJobController;
import com.paypal.kyc.statussynchronization.jobs.KYCUserStatusResyncJob;
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
 * Controller that calls the job to synchronize KYC status from Hyperwallet to Mirakl.
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class KYCStatusResyncJobController extends AbstractJobController {

	private static final String DEFAULT_KYC_USER_STATUS_RESYNC_JOB_NAME = "KYCUserStatusInfoJobSingleExecution";

	/**
	 * Triggers the {@link KYCUserStatusResyncJob} with the {@code delta} time to retrieve
	 * hyperwallet users created since that {@code delta} and schedules the job with the
	 * {@code name} provided.
	 * @param delta the {@link Date} in {@link DateTimeFormat.ISO}
	 * @param name the job name in {@link String}
	 * @return a {@link ResponseEntity <String>} with the name of the job scheduled
	 * @throws SchedulerException if quartz {@link org.quartz.Scheduler} fails
	 */
	@PostMapping("/kyc-userstatus-resync")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date delta,
			@RequestParam(required = false, defaultValue = DEFAULT_KYC_USER_STATUS_RESYNC_JOB_NAME) final String name)
			throws SchedulerException {
		runSingleJob(name, KYCUserStatusResyncJob.class, delta);

		return ResponseEntity.accepted().body(name);
	}

}
