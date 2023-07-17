package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.jobsystem.batchjobsupport.support.AbstractBatchJobItem;
import com.paypal.invoices.extractioncommons.model.AccountingDocumentModel;

/**
 * Base class for all invoice related batch job items.
 *
 * @param <T> the type of the invoice domain object model.
 */
public abstract class AbstractAccountingDocumentBatchJobItem<T extends AccountingDocumentModel>
		extends AbstractBatchJobItem<T> implements BatchJobItem<T> {

	protected AbstractAccountingDocumentBatchJobItem(final T item) {
		super(item);
	}

	protected abstract AbstractAccountingDocumentBatchJobItem<T> from(T item);

}
