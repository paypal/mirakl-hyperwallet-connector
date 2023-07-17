package com.paypal.jobsystem.quartzintegration.listener;

import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobBean;
import com.paypal.jobsystem.quartzintegration.repositories.JobExecutionInformationRepository;
import com.paypal.jobsystem.quartzintegration.repositories.entities.JobExecutionInformationEntity;
import com.paypal.jobsystem.quartzintegration.repositories.entities.JobStatus;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Listener that populates information data in executed jobs
 */
@Component
public class JobExecutionInformationListener extends JobListenerSupport {

	public static final String RUNNING_JOB_ENTITY = "runningInstanceId";

	@Resource
	protected JobExecutionInformationRepository jobExecutionInformationRepository;

	@Resource
	private Scheduler scheduler;

	@Override
	public String getName() {
		return "jobDeltaListener";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jobToBeExecuted(final JobExecutionContext context) {
		getLog().info("Started [{}] at {}", context.getJobDetail().getKey().getName(), LocalDateTime.now());
		saveStartJobExecutionInformation(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
		getLog().info("Finished [{}] at {}", context.getJobDetail().getKey().getName(), LocalDateTime.now());
		saveExecutedJobExecutionInformation(context);
	}

	/**
	 * Stores the {@link JobExecutionInformationEntity} with {@code lastExecutionTime} as
	 * {@link TimeMachine#now()} and {@code job} as the simple name of the job running
	 * @param context the {@link JobExecutionContext}
	 */
	protected void saveStartJobExecutionInformation(final JobExecutionContext context) {
		final JobExecutionInformationEntity jobExecutionInformationEntity = new JobExecutionInformationEntity();
		jobExecutionInformationEntity.setType(getJobClass(context));
		jobExecutionInformationEntity.setName(context.getJobDetail().getKey().getName());
		jobExecutionInformationEntity.setStartTime(DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault()));
		jobExecutionInformationEntity.setStatus(JobStatus.RUNNING);

		final JobExecutionInformationEntity savedInformation = jobExecutionInformationRepository
				.save(jobExecutionInformationEntity);

		final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		jobDataMap.put(RUNNING_JOB_ENTITY, savedInformation);

		final JobDetail jobDetail = context.getJobDetail().getJobBuilder().usingJobData(jobDataMap).build();
		//@formatter:on
		try {
			scheduler.addJob(jobDetail, true);
		}
		catch (final SchedulerException e) {
			getLog().error(e.getMessage(), e);
		}
	}

	private String getJobClass(final JobExecutionContext context) {
		if (QuartzBatchJobBean.class.equals(context.getJobDetail().getJobClass())) {
			return QuartzBatchJobBean.getBatchJobClass(context).getSimpleName();
		}
		else {
			return context.getJobDetail().getJobClass().getSimpleName();
		}
	}

	protected void saveExecutedJobExecutionInformation(final JobExecutionContext context) {
		final JobExecutionInformationEntity savedInformation = (JobExecutionInformationEntity) context.getJobDetail()
				.getJobDataMap().get(RUNNING_JOB_ENTITY);
		savedInformation.setEndTime(DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault()));
		savedInformation.setStatus(JobStatus.COMPLETED);
		jobExecutionInformationRepository.save(savedInformation);
	}

}
