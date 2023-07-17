package com.paypal.jobsystem.quartzadapter.jobcontext;

import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QuartzBatchJobContextFactoryImpl implements QuartzBatchJobContextFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BatchJobContext getBatchJobContext(final BatchJob<BatchJobContext, BatchJobItem<?>> batchJob,
			final JobExecutionContext jobExecutionContext) {
		final QuartzBatchJobContextAdapter quartzBatchJobContextAdapter = getBatchJobContext(jobExecutionContext);
		initializeBatchJobContext(quartzBatchJobContextAdapter, batchJob);

		return quartzBatchJobContextAdapter;
	}

	protected QuartzBatchJobContextAdapter getBatchJobContext(final JobExecutionContext jobExecutionContext) {
		return new QuartzBatchJobContextAdapter(jobExecutionContext);
	}

	private void initializeBatchJobContext(final QuartzBatchJobContextAdapter batchJobContext,
			final BatchJob<BatchJobContext, BatchJobItem<?>> batchJob) {
		batchJobContext.setBatchJob(batchJob);
		batchJobContext.initializeBatchJobUuid();
		batchJobContext.setJobName(getJobName(batchJob, batchJobContext.getJobExecutionContext()));
	}

	private String getJobName(final BatchJob<?, ?> batchJob, final JobExecutionContext context) {
		return batchJob.getClass().getSimpleName()
				+ getManualExecutionTimestamp(context).map("#MANUAL#%s"::formatted).orElse("");
	}

	private Optional<String> getManualExecutionTimestamp(final JobExecutionContext context) {
		return getQuartzJobName(context).map(s -> s.split("_").length > 1 ? s.split("_")[1] : null);
	}

	private Optional<String> getQuartzJobName(final JobExecutionContext context) {
		return Optional.ofNullable(context.getJobDetail().getKey().getName());
	}

}
