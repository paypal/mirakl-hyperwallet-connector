package com.paypal.invoices.extractioncreditnotes.batchjobs;

import com.paypal.invoices.extractioncreditnotes.batchjobs.CreditNoteExtractJobItem;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreditNoteExtractJobItemTest {

	private static final String INVOICE_NUMBER = "001";

	private static final String INVOICE_NUMBER_2 = "002";

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

	@Test
	void from_ShouldReturnANewJobItemWithTheItemProvided() {
		final CreditNoteModel creditNoteModel = CreditNoteModel.builder().invoiceNumber(INVOICE_NUMBER).build();
		final CreditNoteModel creditNoteModel2 = CreditNoteModel.builder().invoiceNumber(INVOICE_NUMBER_2).build();

		final CreditNoteExtractJobItem testObj = new CreditNoteExtractJobItem(creditNoteModel);

		final CreditNoteExtractJobItem result = testObj.from(creditNoteModel2);

		assertThat(result).isNotEqualTo(testObj);
		assertThat(result.getItem().getInvoiceNumber()).isEqualTo(INVOICE_NUMBER_2);
	}

}
