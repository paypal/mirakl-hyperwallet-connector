package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.infraestructure.configuration.InvoicesOperatorCommissionsConfig;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

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

	public static Stream<Arguments> invoicesValues() {
		return Stream.of(Arguments.of(20.00D, 5.00D, 25.00D), Arguments.of(0.00D, 5.00D, 5.00D),
				Arguments.of(10.00D, 0.00D, 10.00D), Arguments.of(7.00D, null, 7.00D),
				Arguments.of(null, 2.00D, 2.00D));
	}

	@ParameterizedTest
	@MethodSource("invoicesValues")
	void convert_shouldConvertAnInvoiceIntoHyperwalletPayment(final Double orderCommissionAmount,
			final Double subscriptionAmount, final Double expectedAmount) {
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
												 .orderCommissionAmountVat(orderCommissionAmount)
												 .subscriptionAmountVat(subscriptionAmount)
												 .currencyIsoCode(CURRENCY_ISO_CODE)
												 .build();
		//@formatter:on

		final HyperwalletPayment result = testObj.convert(invoice);

		assertThat(result.getDestinationToken()).isEqualTo(OPERATOR_BANK_ACOUNT_TOKEN);
		assertThat(result.getClientPaymentId()).isEqualTo(INVOICE_NUMBER + PAYMENT_OPERATOR_SUFFIX);
		assertThat(result.getAmount()).isEqualTo(expectedAmount);
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
				.orderCommissionAmountVat(0.0D)
				.subscriptionAmountVat(0.0D)
				.currencyIsoCode(CURRENCY_ISO_CODE)
				.build();
		//@formatter:on

		final HyperwalletPayment result = testObj.convert(invoice);

		assertThat(result).isNull();
	}

}
