package com.paypal.invoices.extractioncreditnotes.services;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import com.paypal.invoices.extractioncreditnotes.services.MiraklCreditNotesExtractServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklCreditNotesExtractServiceImplTest {

	@InjectMocks
	private MiraklCreditNotesExtractServiceImpl testObj;

	@Mock
	private Converter<MiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverterMock;

	@Test
	void shouldReturnCreditNoteType() {
		assertThat(testObj.getInvoiceType()).isEqualTo(InvoiceTypeEnum.MANUAL_CREDIT);
	}

	@Test
	void shouldReturnCreditNoteConverter() {
		assertThat(testObj.getMiraklInvoiceToAccountingModelConverter())
				.isEqualTo(miraklInvoiceToCreditNoteModelConverterMock);
	}

}
