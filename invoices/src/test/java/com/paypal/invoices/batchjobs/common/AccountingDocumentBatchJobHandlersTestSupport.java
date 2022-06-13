package com.paypal.invoices.batchjobs.common;

import com.paypal.invoices.invoicesextract.model.InvoiceModel;

public abstract class AccountingDocumentBatchJobHandlersTestSupport {

	static class TestAccountingDocumentBatchJobItem extends AbstractAccountingDocumentBatchJobItem<InvoiceModel> {

		public static final String ITEM_TYPE = "Invoice";

		public TestAccountingDocumentBatchJobItem(InvoiceModel item) {
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
		protected TestAccountingDocumentBatchJobItem from(InvoiceModel item) {
			return new TestAccountingDocumentBatchJobItem(item);
		}

	}

}
