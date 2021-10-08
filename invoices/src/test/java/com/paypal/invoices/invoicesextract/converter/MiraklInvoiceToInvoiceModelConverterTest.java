package com.paypal.invoices.invoicesextract.converter;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoice;
import com.paypal.infrastructure.sdk.mirakl.domain.invoice.HMCMiraklInvoiceSummary;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklInvoiceToInvoiceModelConverterTest {

	private static final String INV_NUMBER = "INV-NUMBER";

	private static final String EUR = "EUR";

	private static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(20.00D);

	private static final BigDecimal TOTAL_COMMISSION = BigDecimal.valueOf(10.00D);

	private static final BigDecimal TOTAL_SUBSCRIPTIONS = BigDecimal.valueOf(5.00D);

	private static final Long SHOP_ID = 2000L;

	@InjectMocks
	private MiraklInvoiceToInvoiceModelConverter testObj;

	@Mock
	private HMCMiraklInvoice miraklInvoiceMock;

	@Mock
	private HMCMiraklInvoiceSummary miraklInvoiceSummaryMock;

	@Test
	void convert_shouldConvertMiraklInvoiceIntoInvoiceModel() {
		when(this.miraklInvoiceMock.getId()).thenReturn(INV_NUMBER);
		when(this.miraklInvoiceMock.getShopId()).thenReturn(SHOP_ID);
		when(this.miraklInvoiceMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
		when(this.miraklInvoiceMock.getSummary()).thenReturn(this.miraklInvoiceSummaryMock);
		when(this.miraklInvoiceSummaryMock.getAmountTransferred()).thenReturn(TRANSFER_AMOUNT);
		when(this.miraklInvoiceSummaryMock.getTotalCommissionsIT()).thenReturn(TOTAL_COMMISSION);
		when(this.miraklInvoiceSummaryMock.getTotalSubscriptionIT()).thenReturn(TOTAL_SUBSCRIPTIONS);
		when(this.miraklInvoiceSummaryMock.getAmountTransferredToOperator()).thenReturn(BigDecimal.TEN);

		final InvoiceModel result = this.testObj.convert(this.miraklInvoiceMock);

		assertThat(result.getInvoiceNumber()).isEqualTo(INV_NUMBER);
		assertThat(result.getCurrencyIsoCode()).isEqualTo(EUR);
		assertThat(result.getTransferAmount()).isEqualTo(TRANSFER_AMOUNT.doubleValue());
		assertThat(result.getSubscriptionAmountVat()).isEqualTo(TOTAL_SUBSCRIPTIONS.doubleValue());
		assertThat(result.getOrderCommissionAmountVat()).isEqualTo(TOTAL_COMMISSION.doubleValue());
		assertThat(result.getShopId()).isEqualTo("2000");
		assertThat(result.getTransferAmountToOperator()).isEqualTo(10D);
	}

	@Test
	void convert_shouldReturnNullWhenNullParameterIsReceived() {
		final InvoiceModel result = this.testObj.convert(null);

		assertThat(result).isNull();
	}

}
