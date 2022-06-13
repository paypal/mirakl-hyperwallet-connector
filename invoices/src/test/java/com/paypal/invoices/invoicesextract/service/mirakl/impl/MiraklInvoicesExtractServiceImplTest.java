package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklInvoicesExtractServiceImplTest {

	@InjectMocks
	private MiraklInvoicesExtractServiceImpl testObj;

	@Mock
	private Converter<HMCMiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverterMock;

	@Test
	void shouldReturnCreditNoteType() {
		assertThat(testObj.getInvoiceType()).isEqualTo(InvoiceTypeEnum.AUTO_INVOICE);
	}

	@Test
	void shouldReturnCreditNoteConverter() {
		assertThat(testObj.getMiraklInvoiceToAccountingModelConverter())
				.isEqualTo(miraklInvoiceToInvoiceModelConverterMock);
	}

}
