package com.paypal.invoices.invoicesextract.converter;

import com.hyperwallet.clientsdk.model.HyperwalletPayment;
import com.paypal.invoices.invoicesextract.model.CreditNoteModel;
import com.paypal.invoices.invoicesextract.service.hyperwallet.HyperwalletSDKService;
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
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Test
	void convert_populatesHyperwalletPaymentWithCreditNotesInformationAndPurposeOther() {
		when(hyperwalletSDKServiceMock.getProgramTokenByHyperwalletProgram("myProgramToken"))
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
