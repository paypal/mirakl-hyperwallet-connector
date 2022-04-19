package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperWalletPaymentExtractService;
import org.springframework.stereotype.Service;

/**
 * Credit notes extract batch job item processor for creating credit notes in HW.
 */
@Service
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
