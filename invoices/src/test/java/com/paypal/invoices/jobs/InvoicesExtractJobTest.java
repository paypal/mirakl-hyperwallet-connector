package com.paypal.invoices.jobs;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.invoices.batchjobs.creditnotes.CreditNotesExtractBatchJob;
import com.paypal.invoices.batchjobs.invoices.InvoicesExtractBatchJob;
import com.paypal.invoices.infraestructure.configuration.CreditNotesConfig;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoicesExtractJobTest {

	@InjectMocks
	private InvoicesExtractJob testObj;

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

		when(creditNotesConfigMock.isEnabled()).thenReturn(Boolean.TRUE);

		testObj.execute(jobExecutionContextMock);

		verify(invoicesExtractBatchJobMock).execute(jobExecutionContextMock);
		verify(creditNotesExtractBatchJobMock).execute(jobExecutionContextMock);
	}

	@Test
	void execute_ShouldExecuteInvoicesExtractBatchJobAndNotExecuteCreditNotesExtractBatchJob_WhenCreditConfigIsNotEnabled()
			throws JobExecutionException {

		when(creditNotesConfigMock.isEnabled()).thenReturn(Boolean.FALSE);

		testObj.execute(jobExecutionContextMock);

		verify(invoicesExtractBatchJobMock).execute(jobExecutionContextMock);
		verify(creditNotesExtractBatchJobMock, never()).execute(jobExecutionContextMock);
	}

	@Test
	void beforeItemExtraction_ShouldLogPaymentOfCommissionsEnabledStatus_WhenJobIsAnInvoicesExtractJobAndInvoicesOperatorCommissionsConfigIsNotEnabled()
			throws JobExecutionException {

		when(invoicesOperatorCommissionsConfigMock.isEnabled()).thenReturn(Boolean.FALSE);

		testObj.execute(jobExecutionContextMock);

		assertThat(
				logTrackerStub.contains("Payment of commissions is disabled, skipping processing payments to operator"))
						.isTrue();
	}

	@Test
	void beforeItemExtraction_ShouldLogPaymentOfCommissionsDisabledStatus_WhenJobIsAnInvoicesExtractJobAndInvoicesOperatorCommissionsConfigIsEnabled()
			throws JobExecutionException {

		when(invoicesOperatorCommissionsConfigMock.isEnabled()).thenReturn(Boolean.TRUE);

		testObj.execute(jobExecutionContextMock);

		assertThat(logTrackerStub.contains("Payment of commissions is enabled, retrieving payments for the operator"))
				.isTrue();
	}

}
