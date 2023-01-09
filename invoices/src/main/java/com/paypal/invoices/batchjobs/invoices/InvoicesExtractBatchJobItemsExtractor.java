package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobTrackingService;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the invoices within a delta time from mirakl.
 */
@Service
public class InvoicesExtractBatchJobItemsExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, InvoiceExtractJobItem> {

	private final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService;

	public InvoicesExtractBatchJobItemsExtractor(
			final MiraklAccountingDocumentExtractService<InvoiceModel> miraklAccountingDocumentInvoicesExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklAccountingDocumentInvoicesExtractService = miraklAccountingDocumentInvoicesExtractService;
	}

	/**
	 * Retrieves all the sellers modified since the {@code delta} time and returns them as
	 * a {@link InvoiceExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link InvoiceExtractJobItem}
	 */
	@Override
	protected Collection<InvoiceExtractJobItem> getItems(BatchJobContext ctx, final Date delta) {
		return miraklAccountingDocumentInvoicesExtractService.extractAccountingDocuments(delta).stream()
				.map(InvoiceExtractJobItem::new).collect(Collectors.toList());
	}

}
