package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayeeInvoiceModelToHyperwalletPaymentConverterTest {

	private static final String DESTINATION_TOKEN = "destinationToken";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String SHOP_ID = "2000";

	private static final String INVOICE_NUMBER = "100001000";

	private static final double TRANSFER_AMOUNT = 20.00D;

	private static final String CURRENCY_ISO_CODE = "EUR";

	private static final String PROGRAM_TOKEN = "programToken";

	@InjectMocks
	private PayeeInvoiceModelToHyperwalletPaymentConverter testObj;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Test
	void convert_shouldConvertAnInvoiceIntoHyperwalletPayment() {
		when(hyperwalletSDKServiceMock.getProgramTokenByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(PROGRAM_TOKEN);

		//@formatter:off
		final InvoiceModel invoice = InvoiceModel.builder()
												 .destinationToken(DESTINATION_TOKEN)
												 .shopId(SHOP_ID)
												 .invoiceNumber(INVOICE_NUMBER)
												 .transferAmount(TRANSFER_AMOUNT)
												 .currencyIsoCode(CURRENCY_ISO_CODE)
												 .hyperwalletProgram(HYPERWALLET_PROGRAM)
												 .build();
		//@formatter:on

		final HyperwalletPayment result = testObj.convert(invoice);

		assertThat(result.getDestinationToken()).isEqualTo(DESTINATION_TOKEN);
		assertThat(result.getClientPaymentId()).isEqualTo(INVOICE_NUMBER);
		assertThat(result.getAmount()).isEqualTo(TRANSFER_AMOUNT);
		assertThat(result.getCurrency()).isEqualTo(CURRENCY_ISO_CODE);
		assertThat(result.getPurpose()).isEqualTo("OTHER");
		assertThat(result.getProgramToken()).isEqualTo(PROGRAM_TOKEN);
	}

}
