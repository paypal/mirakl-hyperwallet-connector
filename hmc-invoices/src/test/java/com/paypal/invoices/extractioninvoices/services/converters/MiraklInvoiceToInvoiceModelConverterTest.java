package com.paypal.invoices.extractioninvoices.services.converters;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoice;
import com.mirakl.client.mmp.domain.invoice.MiraklInvoiceSummary;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.invoices.extractioninvoices.services.converters.MiraklInvoiceToInvoiceModelConverter;
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
	private MiraklInvoice miraklInvoiceMock;

	@Mock
	private MiraklInvoiceSummary miraklInvoiceSummaryMock;

	@Test
	void convert_shouldConvertMiraklInvoiceIntoInvoiceModel() {
		when(miraklInvoiceMock.getId()).thenReturn(INV_NUMBER);
		when(miraklInvoiceMock.getShopId()).thenReturn(SHOP_ID);
		when(miraklInvoiceMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
		when(miraklInvoiceMock.getSummary()).thenReturn(miraklInvoiceSummaryMock);
		when(miraklInvoiceSummaryMock.getAmountTransferred()).thenReturn(TRANSFER_AMOUNT);
		when(miraklInvoiceSummaryMock.getTotalCommissionsIT()).thenReturn(TOTAL_COMMISSION);
		when(miraklInvoiceSummaryMock.getTotalSubscriptionIT()).thenReturn(TOTAL_SUBSCRIPTIONS);
		when(miraklInvoiceSummaryMock.getAmountTransferredToOperator()).thenReturn(BigDecimal.TEN);

		final InvoiceModel result = testObj.convert(miraklInvoiceMock);

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
		final InvoiceModel result = testObj.convert(null);

		assertThat(result).isNull();
	}

}
