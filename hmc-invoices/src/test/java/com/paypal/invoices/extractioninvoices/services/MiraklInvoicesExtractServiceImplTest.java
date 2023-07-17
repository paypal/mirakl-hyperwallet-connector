package com.paypal.invoices.extractioninvoices.services;

import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import com.paypal.invoices.extractioninvoices.services.MiraklInvoicesExtractServiceImpl;
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
	private Converter<MiraklInvoice, InvoiceModel> miraklInvoiceToInvoiceModelConverterMock;

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
