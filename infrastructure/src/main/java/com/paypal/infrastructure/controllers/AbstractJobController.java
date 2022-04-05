package com.paypal.infrastructure.controllers;

import com.paypal.infrastructure.job.AbstractDeltaInfoJob;
import com.paypal.infrastructure.service.JobService;
import org.quartz.Job;
import org.quartz.SchedulerException;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Abstract Job class which each job class must extend
 */
public abstract class AbstractJobController {

	@Resource
	protected JobService jobService;

	protected void runSingleJob(final String name, final Class<? extends Job> clazz, final Date delta)
			throws SchedulerException {
		jobService.createAndRunSingleExecutionJob(name, clazz, AbstractDeltaInfoJob.createJobDataMap(delta), null);
	}

}
