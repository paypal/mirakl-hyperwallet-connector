package com.paypal.invoices.extractioncommons.batchjobs;

import com.paypal.invoices.extractioncommons.batchjobs.AbstractAccountingDocumentBatchJobItem;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;

public abstract class AccountingDocumentBatchJobHandlersTestSupport {

	static class TestAccountingDocumentBatchJobItem extends AbstractAccountingDocumentBatchJobItem<InvoiceModel> {

		public static final String ITEM_TYPE = "Invoice";

		public TestAccountingDocumentBatchJobItem(final InvoiceModel item) {
			super(item);
		}

		@Override
		public String getItemId() {
			return getItem().getInvoiceNumber();
		}

		@Override
		public String getItemType() {
			return ITEM_TYPE;
		}

		@Override
		protected TestAccountingDocumentBatchJobItem from(final InvoiceModel item) {
			return new TestAccountingDocumentBatchJobItem(item);
		}

	}

}
