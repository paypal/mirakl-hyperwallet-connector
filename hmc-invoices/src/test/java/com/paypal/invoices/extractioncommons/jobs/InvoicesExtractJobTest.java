package com.paypal.invoices.extractioncommons.jobs;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.jobsystem.batchjob.model.BatchJob;
import com.paypal.jobsystem.quartzadapter.job.QuartzBatchJobAdapterFactory;
import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNotesExtractBatchJob;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoicesExtractBatchJob;
import com.paypal.invoices.extractioncreditnotes.configuration.CreditNotesConfig;
import com.paypal.invoices.extractioninvoices.configuration.InvoicesOperatorCommissionsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractJobTest {

	@InjectMocks
	@Spy
	private MyInvoicesExtractJob testObj;

	@Mock
	private InvoicesExtractBatchJob invoicesExtractBatchJobMock;

	@Mock
	private CreditNotesExtractBatchJob creditNotesExtractBatchJobMock;

	@Mock
	private InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfigMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(InvoicesExtractJob.class);

	@Mock
	private CreditNotesConfig creditNotesConfigMock;

	@Mock
	private JobExecutionContext jobExecutionContextMock;

	@Test
	void execute_ShouldExecuteInvoicesExtractBatchJobAndExecuteCreditNotesExtractBatchJob_WhenCreditConfigIsEnabled()
			throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(invoicesExtractBatchJobMock, jobExecutionContextMock);
		doNothing().when(testObj).executeBatchJob(creditNotesExtractBatchJobMock, jobExecutionContextMock);
		when(creditNotesConfigMock.isEnabled()).thenReturn(Boolean.TRUE);

		testObj.execute(jobExecutionContextMock);

		verify(testObj).executeBatchJob(invoicesExtractBatchJobMock, jobExecutionContextMock);
		verify(testObj).executeBatchJob(creditNotesExtractBatchJobMock, jobExecutionContextMock);
	}

	@Test
	void execute_ShouldExecuteInvoicesExtractBatchJobAndNotExecuteCreditNotesExtractBatchJob_WhenCreditConfigIsNotEnabled()
			throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(invoicesExtractBatchJobMock, jobExecutionContextMock);
		when(creditNotesConfigMock.isEnabled()).thenReturn(Boolean.FALSE);

		testObj.execute(jobExecutionContextMock);

		verify(testObj).executeBatchJob(invoicesExtractBatchJobMock, jobExecutionContextMock);
		verify(testObj, never()).executeBatchJob(creditNotesExtractBatchJobMock, jobExecutionContextMock);
	}

	@Test
	void beforeItemExtraction_ShouldLogPaymentOfCommissionsEnabledStatus_WhenJobIsAnInvoicesExtractJobAndInvoicesOperatorCommissionsConfigIsNotEnabled()
			throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(invoicesExtractBatchJobMock, jobExecutionContextMock);
		when(invoicesOperatorCommissionsConfigMock.isEnabled()).thenReturn(Boolean.FALSE);

		testObj.execute(jobExecutionContextMock);

		assertThat(
				logTrackerStub.contains("Payment of commissions is disabled, skipping processing payments to operator"))
						.isTrue();
	}

	@Test
	void beforeItemExtraction_ShouldLogPaymentOfCommissionsDisabledStatus_WhenJobIsAnInvoicesExtractJobAndInvoicesOperatorCommissionsConfigIsEnabled()
			throws JobExecutionException {
		doNothing().when(testObj).executeBatchJob(invoicesExtractBatchJobMock, jobExecutionContextMock);

		when(invoicesOperatorCommissionsConfigMock.isEnabled()).thenReturn(Boolean.TRUE);

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains("Payment of commissions is enabled, retrieving payments for the operator"))
				.isTrue();
	}

	static class MyInvoicesExtractJob extends InvoicesExtractJob {

		public MyInvoicesExtractJob(final QuartzBatchJobAdapterFactory quartzBatchJobAdapterFactory,
				final InvoicesExtractBatchJob invoicesExtractBatchJob,
				final CreditNotesExtractBatchJob creditNotesExtractBatchJob, final CreditNotesConfig creditNotesConfig,
				final InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfig) {
			super(quartzBatchJobAdapterFactory, invoicesExtractBatchJob, creditNotesExtractBatchJob, creditNotesConfig,
					invoicesOperatorCommissionsConfig);
		}

		@Override
		protected void executeBatchJob(final BatchJob batchJob, final JobExecutionContext context)
				throws JobExecutionException {
			super.executeBatchJob(batchJob, context);
		}

	}

}
