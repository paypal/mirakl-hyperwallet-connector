package com.paypal.invoices.extractioninvoices.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.infrastructure.hyperwallet.services.PaymentHyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paypal.infrastructure.hyperwallet.constants.HyperWalletConstants.PAYMENT_OPERATOR_SUFFIX;
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

	private static final String OPERATOR_BANK_ACCOUNT_TOKEN = "operatorBankAccountToken";

	@InjectMocks
	private OperatorInvoiceModelToHyperwalletPaymentConverter testObj;

	@Mock
	private PaymentHyperwalletSDKService paymentHyperwalletSDKServiceMock;

	@Mock
	private HyperwalletProgramsConfiguration hyperwalletProgramsConfigurationMock;

	@Mock
	private HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration hyperwalletProgramConfigurationMock;

	@Test
	void convert_shouldConvertAnInvoiceIntoHyperwalletPayment() {
		final Double transferAmountToOperator = 100.10D;
		when(paymentHyperwalletSDKServiceMock.getProgramTokenByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(PROGRAM_TOKEN);
		when(hyperwalletProgramsConfigurationMock.getProgramConfiguration(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletProgramConfigurationMock);
		when(hyperwalletProgramConfigurationMock.getBankAccountToken()).thenReturn(OPERATOR_BANK_ACCOUNT_TOKEN);

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

		assertThat(result.getDestinationToken()).isEqualTo(OPERATOR_BANK_ACCOUNT_TOKEN);
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
