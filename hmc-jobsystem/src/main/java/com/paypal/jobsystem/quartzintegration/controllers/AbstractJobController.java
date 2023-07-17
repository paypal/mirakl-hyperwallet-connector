package com.paypal.jobsystem.quartzintegration.controllers;

import com.paypal.jobsystem.quartzintegration.support.AbstractDeltaInfoJob;
import jakarta.annotation.Resource;
import com.paypal.jobsystem.quartzintegration.services.JobService;
import org.quartz.Job;
import org.quartz.SchedulerException;

import java.util.Date;
import java.util.Map;

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

	protected void runSingleJob(final String name, final Class<? extends Job> clazz, final Date delta,
			final Map<String, Object> params) throws SchedulerException {
		jobService.createAndRunSingleExecutionJob(name, clazz, AbstractDeltaInfoJob.createJobDataMap(delta, params),
				null);
	}

}
