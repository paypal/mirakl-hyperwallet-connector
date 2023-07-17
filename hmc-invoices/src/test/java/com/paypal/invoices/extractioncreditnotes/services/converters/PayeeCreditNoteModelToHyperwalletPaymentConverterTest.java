package com.paypal.invoices.extractioncreditnotes.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.extractioncreditnotes.model.CreditNoteModel;
import com.paypal.infrastructure.hyperwallet.services.PaymentHyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayeeCreditNoteModelToHyperwalletPaymentConverterTest {

	@InjectMocks
	private PayeeCreditNoteModelToHyperwalletPaymentConverter testObj;

	@Mock
	private PaymentHyperwalletSDKService paymentHyperwalletSDKServiceMock;

	@Test
	void convert_populatesHyperwalletPaymentWithCreditNotesInformationAndPurposeOther() {
		when(paymentHyperwalletSDKServiceMock.getProgramTokenByHyperwalletProgram("myProgramToken"))
				.thenReturn("the-real-token");

		final CreditNoteModel creditNoteModelStub = CreditNoteModel.builder().destinationToken("destination-token")
				.creditAmount(20.00D).currencyIsoCode("EUR").hyperwalletProgram("myProgramToken")
				.invoiceNumber("invoiceNumber").build();

		final HyperwalletPayment result = testObj.convert(creditNoteModelStub);

		assertThat(result.getAmount()).isEqualTo(20.00D);
		assertThat(result.getCurrency()).isEqualTo("EUR");
		assertThat(result.getProgramToken()).isEqualTo("the-real-token");
		assertThat(result.getDestinationToken()).isEqualTo("destination-token");
		assertThat(result.getClientPaymentId()).isEqualTo("invoiceNumber");
		assertThat(result.getPurpose()).isEqualTo("OTHER");
	}

}
