package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the credit notes within a delta time from
 * mirakl.
 */
@Service
public class CreditNotesExtractBatchJobItemsExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, CreditNoteExtractJobItem> {

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
	 * @return a {@link Collection} of {@link CreditNoteExtractJobItem}
	 */
	@Override
	protected Collection<CreditNoteExtractJobItem> getItems(Date delta) {
		return miraklAccountingDocumentCreditNotesExtractService.extractAccountingDocuments(delta).stream()
				.map(CreditNoteExtractJobItem::new).collect(Collectors.toList());
	}

}
