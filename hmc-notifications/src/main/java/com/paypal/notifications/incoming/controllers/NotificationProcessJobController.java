package com.paypal.notifications.incoming.controllers;

import com.paypal.jobsystem.quartzintegration.controllers.AbstractJobController;
import com.paypal.notifications.incoming.jobs.NotificationProcessJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manually trigger the {@link NotificationProcessJob}.
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class NotificationProcessJobController extends AbstractJobController {

	private static final String DEFAULT_JOB_NAME = "NotificationProcessJobSingleExecution";

	/**
	 * Triggers a single execution of the {@link NotificationProcessJob}.
	 * @param name the Quartz job name for this execution
	 * @return the name of the scheduled job
	 * @throws SchedulerException if the Quartz scheduler fails
	 */
	@PostMapping("/process-notifications")
	public ResponseEntity<String> runJob(
			@RequestParam(required = false, defaultValue = DEFAULT_JOB_NAME) final String name)
			throws SchedulerException {
		runSingleJob(name, NotificationProcessJob.class, null);
		return ResponseEntity.accepted().body(name);
	}

}
