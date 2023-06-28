package com.paypal.jobsystem.quartzadapter.job;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;

public class QuartzBatchJobBuilder extends JobBuilder {

	protected QuartzBatchJobBuilder() {
		super();
	}

	public static <B extends BatchJob> QuartzBatchJobBuilder newJob(final B batchJob) {
		final QuartzBatchJobBuilder builder = new QuartzBatchJobBuilder();
		final JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN, batchJob);
		builder.ofType(QuartzBatchJobBean.class).usingJobData(jobDataMap);

		return builder;
	}

}
