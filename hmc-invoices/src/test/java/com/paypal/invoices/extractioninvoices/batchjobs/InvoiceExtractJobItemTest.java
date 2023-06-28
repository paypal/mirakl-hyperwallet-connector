package com.paypal.invoices.extractioninvoices.batchjobs;

import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.invoices.extractioninvoices.batchjobs.InvoiceExtractJobItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceExtractJobItemTest {

	private static final String INVOICE_NUMBER = "001";

	private static final String INVOICE_NUMBER_2 = "002";

	private static final String ITEM_TYPE = "Invoice";

	@Test
	void getItemId_ShouldReturnInvoiceNumber() {

		final InvoiceModel invoiceModel = InvoiceModel.builder().invoiceNumber(INVOICE_NUMBER).build();
		final InvoiceExtractJobItem testObj = new InvoiceExtractJobItem(invoiceModel);

		assertThat(testObj.getItemId()).isEqualTo(INVOICE_NUMBER);
	}

	@Test
	void getItemType_ShouldReturnInvoice() {

		final InvoiceModel invoiceModel = InvoiceModel.builder().invoiceNumber(INVOICE_NUMBER).build();
		final InvoiceExtractJobItem testObj = new InvoiceExtractJobItem(invoiceModel);

		assertThat(testObj.getItemType()).isEqualTo(ITEM_TYPE);
	}

	@Test
	void from_ShouldReturnANewJobItemWithTheItemProvided() {
		final InvoiceModel invoiceModel = InvoiceModel.builder().invoiceNumber(INVOICE_NUMBER).build();
		final InvoiceModel invoiceModel2 = InvoiceModel.builder().invoiceNumber(INVOICE_NUMBER_2).build();

		final InvoiceExtractJobItem testObj = new InvoiceExtractJobItem(invoiceModel);

		final InvoiceExtractJobItem result = testObj.from(invoiceModel2);

		assertThat(result).isNotEqualTo(testObj);
		assertThat(result.getItem().getInvoiceNumber()).isEqualTo(INVOICE_NUMBER_2);
	}

}
