package com.paypal.invoices.extractioncommons.jobs;

import com.paypal.jobsystem.quartzadapter.support.AbstractBatchJobSupportQuartzJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJob;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesExtractBatchJob;
import com.paypal.invoices.extractioncreditnotes.configuration.CreditNotesConfig;
import com.paypal.invoices.extractioninvoices.configuration.InvoicesOperatorCommissionsConfig;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * Quartz Job for executing the {@link InvoicesExtractBatchJob} and
 * {@link CreditNotesExtractBatchJob}.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class InvoicesExtractJob extends AbstractBatchJobSupportQuartzJob implements Job {

	private final InvoicesExtractBatchJob invoicesExtractBatchJob;

	private final CreditNotesExtractBatchJob creditNotesExtractBatchJob;

	private final CreditNotesConfig creditNotesConfig;

	private final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig;

	public InvoicesExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
			final InvoicesExtractBatchJob invoicesExtractBatchJob,
			final CreditNotesExtractBatchJob creditNotesExtractBatchJob, final CreditNotesConfig creditNotesConfig,
			final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig) {
		super(quartzBatchJobAdapterFactory);
		this.invoicesExtractBatchJob = invoicesExtractBatchJob;
		this.creditNotesExtractBatchJob = creditNotesExtractBatchJob;
		this.creditNotesConfig = creditNotesConfig;
		this.invoicesOperatorCommissionsConfig = invoicesOperatorCommissionsConfig;
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		logCommissionsConfig();
		executeBatchJob(invoicesExtractBatchJob, context);
		if (creditNotesConfig.isEnabled()) {
			executeBatchJob(creditNotesExtractBatchJob, context);
		}
	}

	private void logCommissionsConfig() {
		if (invoicesOperatorCommissionsConfig.isEnabled()) {
			log.info("Payment of commissions is enabled, retrieving payments for the operator");
		}
		else {
			log.info("Payment of commissions is disabled, skipping processing payments to operator");
		}

	}

}
