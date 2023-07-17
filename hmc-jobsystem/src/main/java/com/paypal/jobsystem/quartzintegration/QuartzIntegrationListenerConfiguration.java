package com.paypal.jobsystem.quartzintegration;

import com.paypal.jobsystem.quartzintegration.listener.JobExecutionInformationListener;
import com.paypal.jobsystem.quartzintegration.listener.SameJobVetoingListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerListener;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to attach listeners to jobs
 */
@Configuration
public class QuartzIntegrationListenerConfiguration {

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
