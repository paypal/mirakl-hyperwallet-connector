package com.paypal.invoices.batchjobs.invoices;

import com.paypal.invoices.invoicesextract.model.InvoiceModel;
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

		InvoiceExtractJobItem result = testObj.from(invoiceModel2);

		assertThat(result).isNotEqualTo(testObj);
		assertThat(result.getItem().getInvoiceNumber()).isEqualTo(INVOICE_NUMBER_2);
	}

}
