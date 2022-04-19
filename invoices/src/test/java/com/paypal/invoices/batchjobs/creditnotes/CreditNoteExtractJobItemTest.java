package com.paypal.invoices.batchjobs.creditnotes;

import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreditNoteExtractJobItemTest {

	private static final String INVOICE_NUMBER = "002";

	private static final String ITEM_TYPE = "CreditNote";

	@Test
	void getItemId_ShouldReturnInvoiceNumber() {

		final CreditNoteModel creditNoteModel = CreditNoteModel.builder().invoiceNumber(INVOICE_NUMBER).build();
		final CreditNoteExtractJobItem testObj = new CreditNoteExtractJobItem(creditNoteModel);

		assertThat(testObj.getItemId()).isEqualTo(INVOICE_NUMBER);
	}

	@Test
	void getItemType_ShouldReturnCreditNote() {

		final CreditNoteModel creditNoteModel = CreditNoteModel.builder().invoiceNumber(INVOICE_NUMBER).build();
		final CreditNoteExtractJobItem testObj = new CreditNoteExtractJobItem(creditNoteModel);

		assertThat(testObj.getItemType()).isEqualTo(ITEM_TYPE);
	}

}
