package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.infrastructure.batchjob.AbstractBatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;

/**
 * Class that holds the needed information for batch processing {@link CreditNoteModel}
 */
public class CreditNoteExtractJobItem extends AbstractBatchJobItem<CreditNoteModel> {

	public CreditNoteExtractJobItem(CreditNoteModel item) {
		super(item);
	}

	/**
	 * Returns the {@code invoiceNumber} of a {@link CreditNoteModel}
	 * @return the {@link String} invoice number
	 */
	@Override
	public String getItemId() {
		return getItem().getInvoiceNumber();
	}

	/**
	 * Returns the type as {@link String} of the {@link BatchJobItem}
	 * @return {@code CreditNoteModel}
	 */
	@Override
	public String getItemType() {
		return "CreditNote";
	}

}
