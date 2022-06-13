package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
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
	private Converter<HMCMiraklInvoice, CreditNoteModel> miraklInvoiceToCreditNoteModelConverterMock;

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
