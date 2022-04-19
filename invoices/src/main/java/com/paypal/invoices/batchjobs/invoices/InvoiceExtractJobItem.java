package com.paypal.invoices.batchjobs.invoices;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;

/**
 * Class that holds the needed information for batch processing {@link InvoiceModel}
 */
public class InvoiceExtractJobItem extends AbstractBatchJobItem<InvoiceModel> {

	public InvoiceExtractJobItem(InvoiceModel item) {
		super(item);
	}

	/**
	 * Returns the {@code invoiceNumber} of a {@link InvoiceModel}
	 * @return the {@link String} invoice number
	 */
	@Override
	public String getItemId() {
		return getItem().getInvoiceNumber();
	}

	/**
	 * Returns the type as {@link String} of the {@link BatchJobItem}
	 * @return {@code InvoiceModel}
	 */
	@Override
	public String getItemType() {
		return "Invoice";
	}

}
