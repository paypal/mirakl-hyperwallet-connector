package com.paypal.notifications.controllers;

import com.paypal.infrastructure.controllers.AbstractJobController;
import com.paypal.notifications.jobs.NotificationProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Specific controller to fire a notification retry process
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class NotificationProcessJobController extends AbstractJobController {

	private static final String DEFAULT_NOTIFICATION_PROCESS_JOB_NAME = "NotificationProcessJobSingleExecution";

	/**
	 * Triggers the {@link NotificationProcessJob} with the {@code delta} time to retrieve
	 * invoices created or updated since that {@code delta} and schedules the job with the
	 * {@code name} provided
	 * @param name the job name in {@link String}
	 * @return a {@link ResponseEntity <String>} with the name of the job scheduled
	 * @throws SchedulerException if quartz {@link org.quartz.Scheduler} fails
	 */
	@PostMapping("/process-failed-notifications")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false, defaultValue = DEFAULT_NOTIFICATION_PROCESS_JOB_NAME) final String name)
			throws SchedulerException {
		runSingleJob(name, NotificationProcessJob.class, null);

		return ResponseEntity.accepted().body(name);
	}

}
