package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncommons.batchjobs.AbstractAccountingDocumentBatchJobExtractor;
import com.paypal.invoices.extractioncommons.services.MiraklAccountingDocumentExtractService;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the credit notes within a delta time from
 * mirakl.
 */
@Component
public class CreditNotesExtractBatchJobItemsExtractor
		extends AbstractAccountingDocumentBatchJobExtractor<BatchJobContext, CreditNoteExtractJobItem> {

	private final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService;

	public CreditNotesExtractBatchJobItemsExtractor(
			final MiraklAccountingDocumentExtractService<CreditNoteModel> miraklAccountingDocumentCreditNotesExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklAccountingDocumentCreditNotesExtractService = miraklAccountingDocumentCreditNotesExtractService;
	}

	/**
	 * Retrieves all the sellers modified since the {@code delta} time and returns them as
	 * a {@link CreditNoteExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @param includePaid whether to include paid invoices or not
	 * @return a {@link Collection} of {@link CreditNoteExtractJobItem}
	 */
	@Override
	protected Collection<CreditNoteExtractJobItem> getItems(final BatchJobContext ctx, final Date delta,
			final boolean includePaid) {
		return miraklAccountingDocumentCreditNotesExtractService.extractAccountingDocuments(delta, includePaid).stream()
				.map(CreditNoteExtractJobItem::new).collect(Collectors.toList());
	}

}
