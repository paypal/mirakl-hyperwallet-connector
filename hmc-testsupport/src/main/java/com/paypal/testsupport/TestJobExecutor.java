package com.paypal.testsupport;

import com.paypal.infrastructure.support.date.DateUtil;
import com.paypal.infrastructure.support.date.TimeMachine;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Component
public class TestJobExecutor {

	private final Scheduler scheduler;

	protected static final Set<String> runningJobs = ConcurrentHashMap.newKeySet();

	protected final JobRunningListener jobRunningListener = new JobRunningListener();

	public TestJobExecutor(final Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void executeJobAndWaitForCompletion(@NonNull final Class<? extends Job> clazz) {
		executeJobAndWaitForCompletion(clazz, Map.of());
	}

	public void executeJobAndWaitForCompletion(@NonNull final Class<? extends Job> clazz,
			@NonNull final Map<String, Object> parameters) {
		//@formatter:off
		final Date scheduling = DateUtil.convertToDate(TimeMachine.now(), ZoneId.systemDefault());

		final String jobName = clazz.getSimpleName() + "-" + System.nanoTime();
		final JobDetail jobExecution = JobBuilder.newJob(clazz)
				.withIdentity(jobName)
				.usingJobData(new JobDataMap(parameters))
				.storeDurably()
				.build();

		final Trigger singleJobExecutionTrigger = TriggerBuilder.newTrigger()
				.forJob(jobExecution)
				.startAt(scheduling)
				.build();
		//@formatter:on
		try {
			if (!scheduler.getListenerManager().getJobListeners().contains(jobRunningListener)) {
				scheduler.getListenerManager().addJobListener(jobRunningListener);
			}
			scheduler.addJob(jobExecution, true);
			runningJobs.add(jobName);
			scheduler.scheduleJob(singleJobExecutionTrigger);
		}
		catch (final SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
		waitForJobToFinish(jobName);
	}

	protected void waitForJobToFinish(final String jobName) {
		await().atMost(2, TimeUnit.MINUTES).until(() -> !runningJobs.contains(jobName));
	}

	protected static class JobRunningListener extends JobListenerSupport {

		@Override
		public String getName() {
			return "jobRunningListener";
		}

		@Override
		public void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
			runningJobs.remove(context.getJobDetail().getKey().getName());
		}

	}

}
