package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioncommons.batchjobs.AbstractAccountingDocumentBatchJobExtractor;
import com.paypal.invoices.extractioncommons.services.MiraklAccountingDocumentExtractService;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * The extractor class for retrieving all the invoices within a delta time from mirakl.
 */
@Component
public class InvoicesExtractBatchJobItemsExtractor
		extends AbstractAccountingDocumentBatchJobExtractor<BatchJobContext, InvoiceExtractJobItem> {

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
	 * @param includePaid whether to include paid invoices or not
	 * @return a {@link Collection} of {@link InvoiceExtractJobItem}
	 */
	@Override
	protected Collection<InvoiceExtractJobItem> getItems(final BatchJobContext ctx, final Date delta,
			final boolean includePaid) {
		return miraklAccountingDocumentInvoicesExtractService.extractAccountingDocuments(delta, includePaid).stream()
				.map(InvoiceExtractJobItem::new).collect(Collectors.toList());
	}

}
