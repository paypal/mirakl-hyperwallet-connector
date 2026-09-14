package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.springframework.util.ClassUtils;

public class QuartzBatchJobBuilder extends JobBuilder {

	protected QuartzBatchJobBuilder() {
		super();
	}

	/**
	 * Creates a builder for the Quartz job that runs the given {@link BatchJob}.
	 * <p>
	 * Only the fully qualified class name of the batch job is put in the job data map,
	 * never the instance itself. Quartz serializes the job data map when a JDBC
	 * {@code JobStore} is configured, and a batch job transitively references the whole
	 * Spring service graph, which is not serializable. The bean is looked up again from
	 * the application context when the job runs, so the same job definition works with
	 * both {@code RAMJobStore} and a JDBC {@code JobStore}.
	 * @param batchJob the batch job bean to run.
	 * @return the builder.
	 */
	public static <B extends BatchJob> QuartzBatchJobBuilder newJob(final B batchJob) {
		final QuartzBatchJobBuilder builder = new QuartzBatchJobBuilder();
		final JobDataMap jobDataMap = new JobDataMap();
		// getUserClass unwraps a CGLIB proxy, so the recorded name is the one the bean can
		// be looked up by rather than an unloadable generated proxy name.
		jobDataMap.put(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN, ClassUtils.getUserClass(batchJob).getName());
		builder.ofType(QuartzBatchJobBean.class).usingJobData(jobDataMap);

		return builder;
	}

}
