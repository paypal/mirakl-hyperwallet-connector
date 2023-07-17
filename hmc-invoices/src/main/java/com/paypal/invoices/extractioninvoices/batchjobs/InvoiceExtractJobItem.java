package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.invoices.extractioncommons.batchjobs.AbstractAccountingDocumentBatchJobItem;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;

/**
 * Class that holds the needed information for batch processing {@link InvoiceModel}
 */
public class InvoiceExtractJobItem extends AbstractAccountingDocumentBatchJobItem<InvoiceModel> {

	public static final String ITEM_TYPE = "Invoice";

	public InvoiceExtractJobItem(final InvoiceModel item) {
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
		return ITEM_TYPE;
	}

	@Override
	protected InvoiceExtractJobItem from(final InvoiceModel item) {
		return new InvoiceExtractJobItem(item);
	}

}
