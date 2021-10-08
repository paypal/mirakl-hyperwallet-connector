package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paypal.infrastructure.constants.HyperWalletConstants.PAYMENT_OPERATOR_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorInvoiceModelToHyperwalletPaymentConverterTest {

	private static final String DESTINATION_TOKEN = "destinationToken";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String SHOP_ID = "2000";

	private static final String INVOICE_NUMBER = "100001000";

	private static final String CURRENCY_ISO_CODE = "EUR";

	private static final String PROGRAM_TOKEN = "programToken";

	private static final String OPERATOR_BANK_ACOUNT_TOKEN = "operatorBankAccountToken";

	@InjectMocks
	private OperatorInvoiceModelToHyperwalletPaymentConverter testObj;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private InvoicesOperatorCommissionsConfig invoicesOperatorCommissionsConfigMock;

	@Test
	void convert_shouldConvertAnInvoiceIntoHyperwalletPayment() {
		final Double transferAmountToOperator = 100.10D;
		when(hyperwalletSDKServiceMock.getProgramTokenByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(PROGRAM_TOKEN);
		when(invoicesOperatorCommissionsConfigMock.getBankAccountToken(HYPERWALLET_PROGRAM))
				.thenReturn(OPERATOR_BANK_ACOUNT_TOKEN);

		//@formatter:off
		final InvoiceModel invoice = InvoiceModel.builder()
												 .hyperwalletProgram(HYPERWALLET_PROGRAM)
												 .destinationToken(DESTINATION_TOKEN)
												 .shopId(SHOP_ID)
												 .invoiceNumber(INVOICE_NUMBER)
												 .transferAmountToOperator(transferAmountToOperator)
												 .currencyIsoCode(CURRENCY_ISO_CODE)
												 .build();
		//@formatter:on

		final HyperwalletPayment result = testObj.convert(invoice);

		assertThat(result.getDestinationToken()).isEqualTo(OPERATOR_BANK_ACOUNT_TOKEN);
		assertThat(result.getClientPaymentId()).isEqualTo(INVOICE_NUMBER + PAYMENT_OPERATOR_SUFFIX);
		assertThat(result.getAmount()).isEqualTo(transferAmountToOperator);
		assertThat(result.getCurrency()).isEqualTo(CURRENCY_ISO_CODE);
		assertThat(result.getPurpose()).isEqualTo("OTHER");
		assertThat(result.getProgramToken()).isEqualTo(PROGRAM_TOKEN);
	}

	@Test
	void convert_shouldReturnNullWhenCommissionIsZero() {
		//@formatter:off
		final InvoiceModel invoice = InvoiceModel.builder()
				.destinationToken(DESTINATION_TOKEN)
				.shopId(SHOP_ID)
				.invoiceNumber(INVOICE_NUMBER)
				.transferAmountToOperator(0.0D)
				.currencyIsoCode(CURRENCY_ISO_CODE)
				.build();
		//@formatter:on

		final HyperwalletPayment result = testObj.convert(invoice);

		assertThat(result).isNull();
	}

}
