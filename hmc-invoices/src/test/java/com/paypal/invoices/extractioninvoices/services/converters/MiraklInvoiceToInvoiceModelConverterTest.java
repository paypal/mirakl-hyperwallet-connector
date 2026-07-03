package com.paypal.invoices.extractioninvoices.services.converters;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycle;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleShop;
import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklSellerBillingCycleSummary;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
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
class MiraklInvoiceToInvoiceModelConverterTest {

	private static final UUID INV_NUMBER = UUID.randomUUID();

	private static final String EUR = "EUR";

	private static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(20.00D);

	private static final BigDecimal TOTAL_COMMISSION = BigDecimal.valueOf(10.00D);

	private static final BigDecimal TOTAL_SUBSCRIPTIONS = BigDecimal.valueOf(5.00D);

	private static final Long SHOP_ID = 2000L;

	@InjectMocks
	private MiraklInvoiceToInvoiceModelConverter testObj;

	@Mock
	private MiraklSellerBillingCycle billingCycleMock;

	@Mock
	private MiraklSellerBillingCycleSummary summaryMock;

	@Mock
	private MiraklSellerBillingCycleShop shopMock;

	@Test
	void convert_shouldConvertMiraklInvoiceIntoInvoiceModel() {
		when(billingCycleMock.getId()).thenReturn(INV_NUMBER);
		when(billingCycleMock.getShop()).thenReturn(shopMock);
		when(shopMock.getShopId()).thenReturn(SHOP_ID);
		when(billingCycleMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
		when(billingCycleMock.getSummary()).thenReturn(summaryMock);
		when(billingCycleMock.getAmountTransferredToSeller()).thenReturn(TRANSFER_AMOUNT);
		when(summaryMock.getTotalCommissionsIT()).thenReturn(TOTAL_COMMISSION);
		when(summaryMock.getTotalSubscriptionIT()).thenReturn(TOTAL_SUBSCRIPTIONS);
		when(billingCycleMock.getAmountTransferredToOperator()).thenReturn(BigDecimal.TEN);

		final InvoiceModel result = testObj.convert(billingCycleMock);

		assertThat(result.getInvoiceNumber()).isEqualTo(INV_NUMBER.toString());
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
