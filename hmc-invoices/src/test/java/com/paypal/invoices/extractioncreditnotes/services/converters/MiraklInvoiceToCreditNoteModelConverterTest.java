package com.paypal.invoices.extractioncreditnotes.services.converters;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.paypal.invoices.extractioncreditnotes.services.converters.MiraklInvoiceToCreditNoteModelConverter;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklInvoiceToCreditNoteModelConverterTest {

	@InjectMocks
	private MiraklInvoiceToCreditNoteModelConverter testObj;

	@Test
	void convert_shouldConvertFromMiraklInvoiceToCreditNoteModel() {
		final MiraklInvoice miraklInvoiceStub = new MiraklInvoice();
		miraklInvoiceStub.setId("2000050");
		miraklInvoiceStub.setShopId(2000L);
		miraklInvoiceStub.setTotalChargedAmount(BigDecimal.valueOf(20.00D));
		miraklInvoiceStub.setCurrencyIsoCode(MiraklIsoCurrencyCode.EUR);

		final CreditNoteModel result = testObj.convert(miraklInvoiceStub);

		assertThat(result.getInvoiceNumber()).isEqualTo("2000050");
		assertThat(result.getShopId()).isEqualTo("2000");
		assertThat(result.getCreditAmount()).isEqualTo(20.00D);
		assertThat(result.getCurrencyIsoCode()).isEqualTo("EUR");
	}

}
