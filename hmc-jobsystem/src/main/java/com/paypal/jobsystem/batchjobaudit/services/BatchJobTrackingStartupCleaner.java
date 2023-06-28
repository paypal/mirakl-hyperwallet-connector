package com.paypal.jobsystem.batchjobaudit.services;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BatchJobTrackingStartupCleaner {

	private final BatchJobTrackingService batchJobTrackingService;

	public BatchJobTrackingStartupCleaner(final BatchJobTrackingService batchJobTrackingService) {
		this.batchJobTrackingService = batchJobTrackingService;
	}

	/**
	 * Clean unfinished jobs when application start up.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void cleanUnfinishedJobs() {
		batchJobTrackingService.markNonFinishedJobsAsAborted();
	}

}
