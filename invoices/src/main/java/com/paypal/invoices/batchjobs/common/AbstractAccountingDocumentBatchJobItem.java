package com.paypal.invoices.batchjobs.common;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;

/**
 * Base class for all invoice related batch job items.
 *
 * @param <T> the type of the invoice domain object model.
 */
public abstract class AbstractAccountingDocumentBatchJobItem<T extends AccountingDocumentModel>
		extends AbstractBatchJobItem<T> implements BatchJobItem<T> {

	protected AbstractAccountingDocumentBatchJobItem(T item) {
		super(item);
	}

	protected abstract AbstractAccountingDocumentBatchJobItem<T> from(T item);

}
