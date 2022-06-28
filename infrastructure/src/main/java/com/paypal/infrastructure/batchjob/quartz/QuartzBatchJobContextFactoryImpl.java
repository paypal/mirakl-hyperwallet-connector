package com.paypal.infrastructure.batchjob.quartz;

import com.paypal.infrastructure.batchjob.BatchJob;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuartzBatchJobContextFactoryImpl implements QuartzBatchJobContextFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BatchJobContext getBatchJobContext(BatchJob<BatchJobContext, BatchJobItem<?>> batchJob,
			JobExecutionContext jobExecutionContext) {
		QuartzBatchJobContextAdapter quartzBatchJobContextAdapter = getBatchJobContext(jobExecutionContext);
		initializeBatchJobContext(quartzBatchJobContextAdapter, batchJob);

		return quartzBatchJobContextAdapter;
	}

	protected QuartzBatchJobContextAdapter getBatchJobContext(JobExecutionContext jobExecutionContext) {
		return new QuartzBatchJobContextAdapter(jobExecutionContext);
	}

	private void initializeBatchJobContext(QuartzBatchJobContextAdapter batchJobContext,
			BatchJob<BatchJobContext, BatchJobItem<?>> batchJob) {
		batchJobContext.setBatchJob(batchJob);
		batchJobContext.initializeBatchJobUuid();
		batchJobContext.setJobName(getJobName(batchJob, batchJobContext.getJobExecutionContext()));
	}

	private String getJobName(BatchJob<?, ?> batchJob, JobExecutionContext context) {
		return batchJob.getClass().getSimpleName()
				+ getManualExecutionTimestamp(context).map(s -> String.format("#MANUAL#%s", s)).orElse("");
	}

	private Optional<String> getManualExecutionTimestamp(JobExecutionContext context) {
		return getQuartzJobName(context).map(s -> s.split("_").length > 1 ? s.split("_")[1] : null);
	}

	private Optional<String> getQuartzJobName(JobExecutionContext context) {
		return Optional.ofNullable(context.getJobDetail().getKey().getName());
	}

}
