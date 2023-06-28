package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractDynamicWindowDeltaBatchJobItemsExtractor;

import java.util.Collection;
import java.util.Date;

import static com.paypal.invoices.extractioncommons.controllers.InvoiceExtractJobController.INCLUDE_PAID;

/**
 * Base class Accounting Document batch job items extraction. That allows to extract items
 * based on a delta date and a flag to include paid invoices.
 *
 * @param <C>
 * @param <T>
 */
public abstract class AbstractAccountingDocumentBatchJobExtractor<C extends BatchJobContext, T extends BatchJobItem<?>>
		extends AbstractDynamicWindowDeltaBatchJobItemsExtractor<C, T> {

	protected AbstractAccountingDocumentBatchJobExtractor(final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
	}

	private boolean getIncludePaidParam(final C ctx) {
		final Boolean includePaid = (Boolean) ctx.getJobExecutionContext().getJobDetail().getJobDataMap()
				.get(INCLUDE_PAID);

		return includePaid != null && includePaid;
	}

	@Override
	protected Collection<T> getItems(final C ctx, final Date delta) {
		return getItems(ctx, delta, getIncludePaidParam(ctx));
	}

	protected abstract Collection<T> getItems(final C ctx, final Date delta, final boolean includePaid);

}
