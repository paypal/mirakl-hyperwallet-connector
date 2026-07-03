package com.paypal.invoices.extractioncreditnotes.services.converters;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleShop;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklInvoiceToCreditNoteModelConverterTest {

	@InjectMocks
	private MiraklInvoiceToCreditNoteModelConverter testObj;

	@Mock
	private MiraklSellerBillingCycle billingCycleMock;

	@Mock
	private MiraklSellerBillingCycleShop shopMock;

	@Test
	void convert_shouldConvertFromMiraklInvoiceToCreditNoteModel() {
		final UUID id = UUID.randomUUID();
		when(billingCycleMock.getId()).thenReturn(id);
		when(billingCycleMock.getShop()).thenReturn(shopMock);
		when(shopMock.getShopId()).thenReturn(2000L);
		when(billingCycleMock.getAmountTransferredToSeller()).thenReturn(BigDecimal.valueOf(20.00D));
		when(billingCycleMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);

		final CreditNoteModel result = testObj.convert(billingCycleMock);

		assertThat(result.getInvoiceNumber()).isEqualTo(id.toString());
		assertThat(result.getShopId()).isEqualTo("2000");
		assertThat(result.getCreditAmount()).isEqualTo(20.00D);
		assertThat(result.getCurrencyIsoCode()).isEqualTo("EUR");
	}

}
