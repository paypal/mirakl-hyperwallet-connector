package com.paypal.infrastructure.configuration;

import com.paypal.infrastructure.job.listener.JobExecutionInformationListener;
import com.paypal.infrastructure.job.listener.SameJobVetoingListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerListener;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Configuration class to attach listeners to jobs
 */
@Configuration
public class JobListenerConfig {

	@Resource
	private Scheduler scheduler;

	@Resource
	private JobExecutionInformationListener jobExecutionInformationListener;

	@Resource
	private TriggerListener sameJobVetoingListener;

	/**
	 * Adds {@link JobExecutionInformationListener} to all jobs in the system
	 * @throws SchedulerException if Quartz fails
	 */
	@PostConstruct
	public void jobJobExecutionInformationListenerInit() throws SchedulerException {
		scheduler.getListenerManager().addJobListener(jobExecutionInformationListener);
	}

	/**
	 * Adds {@link SameJobVetoingListener} to all jobs in the system
	 * @throws SchedulerException if Quartz fails
	 */
	@PostConstruct
	public void triggerSameJobVetoingListener() throws SchedulerException {
		scheduler.getListenerManager().addTriggerListener(sameJobVetoingListener);
	}

}
