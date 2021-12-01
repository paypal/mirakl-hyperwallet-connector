package com.paypal.infrastructure.service;

import com.paypal.infrastructure.model.job.JobStatus;
import com.paypal.infrastructure.util.DateUtil;
import com.paypal.infrastructure.util.TimeMachine;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class to operate with Quartz jobs
 */
@Slf4j
@Service
public class JobService {

	@Resource
	private Scheduler scheduler;

	/**
	 * Returns a {@link List} of {@link JobDetail} of all jobs with an scheduling plan
	 * @return {@link List} of {@link JobDetail}
	 * @throws SchedulerException
	 */
	public Set<JobDetail> getJobs() throws SchedulerException {
		//@formatter:off
		return scheduler.getJobKeys(GroupMatcher.anyGroup()).stream()
				.map(getJobKeyJobDetailFunction())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		//@formatter:on*/
	}

	/**
	 * Returns the status of an specific job by Name
	 * @param name Job name
	 * @return the {@link JobStatus}
	 * @throws SchedulerException
	 */
	public JobStatus getJobStatus(@NonNull final String name) throws SchedulerException {
		//@formatter:off
		return getJobs().stream()
				.filter(jd -> name.equals(jd.getKey().getName()))
				.map(JobDetail::getJobDataMap)
				.filter(jobDataMap -> Objects.nonNull(jobDataMap) && jobDataMap.containsKey("status"))
				.map(jobDataMap -> jobDataMap.get("status"))
				.filter(JobStatus.class::isInstance)
				.map(JobStatus.class::cast)
				.findAny()
				.orElse(JobStatus.UNKNOWN);
		//@formatter:on
	}

	/**
	 * Creates a job of an existing {@link Job} class to schedule it and run it now
	 * @param name Job name
	 * @param clazz must be of type {@link Job}
	 * @param jobDataMap any additional parameters for the job
	 * @param schedule if empty schedules the job to be triggered now
	 * @throws SchedulerException
	 */
	public void createAndRunSingleExecutionJob(@NonNull final String name, @NonNull final Class<? extends Job> clazz,
			final JobDataMap jobDataMap, @Nullable final Date schedule) throws SchedulerException {
		//@formatter:off
		final Date scheduling = Optional.ofNullable(schedule)
				.orElse(DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault()));

		final JobDetail jobExecution = JobBuilder.newJob(clazz)
				.withIdentity(name)
				.usingJobData(jobDataMap)
				.storeDurably()
				.build();

		final Trigger singleJobExecutionTrigger = TriggerBuilder.newTrigger()
				.forJob(jobExecution)
				.startAt(scheduling)
				.build();
		//@formatter:on
		scheduler.addJob(jobExecution, true);
		scheduler.scheduleJob(singleJobExecutionTrigger);
	}

	protected Function<JobKey, JobDetail> getJobKeyJobDetailFunction() {
		return jk -> {
			try {
				return scheduler.getJobDetail(jk);
			}
			catch (final SchedulerException e) {
				log.error(e.getMessage(), e);
			}
			return null;
		};
	}

}
