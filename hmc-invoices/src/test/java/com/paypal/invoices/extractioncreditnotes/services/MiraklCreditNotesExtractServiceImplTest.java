package com.paypal.invoices.extractioncreditnotes.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.invoices.extractioncommons.model.InvoiceTypeEnum;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MiraklCreditNotesExtractServiceImplTest {

	@InjectMocks
	private MiraklCreditNotesExtractServiceImpl testObj;

	@Mock
	private Converter<MiraklSellerBillingCycle, CreditNoteModel> miraklInvoiceToCreditNoteModelConverterMock;

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
