package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncommons.services.HyperWalletPaymentExtractService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Credit notes extract batch job item processor for creating credit notes in HW.
 */
@Component
public class CreditNotesExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, CreditNoteExtractJobItem> {

	private final HyperWalletPaymentExtractService hyperWalletPaymentExtractService;

	public CreditNotesExtractBatchJobItemProcessor(
			final HyperWalletPaymentExtractService hyperWalletPaymentExtractService) {
		this.hyperWalletPaymentExtractService = hyperWalletPaymentExtractService;
	}

	/**
	 * Processes the {@link CreditNoteExtractJobItem} with the
	 * {@link HyperWalletPaymentExtractService}
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link CreditNoteExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final CreditNoteExtractJobItem jobItem) {
		hyperWalletPaymentExtractService.payPayeeCreditNotes(jobItem.getItem());
	}

}
