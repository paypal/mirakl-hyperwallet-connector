package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;

public class QuartzBatchJobBuilder extends JobBuilder {

	protected QuartzBatchJobBuilder() {
		super();
	}

	public static <B extends BatchJob> QuartzBatchJobBuilder newJob(B batchJob) {
		QuartzBatchJobBuilder builder = new QuartzBatchJobBuilder();
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(QuartzBatchJobBean.KEY_BATCH_JOB_BEAN, batchJob);
		builder.ofType(QuartzBatchJobBean.class).usingJobData(jobDataMap);

		return builder;
	}

}
