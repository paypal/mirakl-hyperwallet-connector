package com.paypal.infrastructure.sdk.mirakl.domain.invoice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCMiraklInvoicesTest {

	@Mock
	private HMCMiraklInvoice hmcMiraklInvoiceOneMock, hmcMiraklInvoiceTwoMock;

	@Test
	void getInvoices_shouldReturnWhatItWasSet() {
		final HMCMiraklInvoices testObj = new HMCMiraklInvoices();
		testObj.setHmcInvoices(List.of(hmcMiraklInvoiceOneMock, hmcMiraklInvoiceTwoMock));

		assertThat(testObj.getHmcInvoices()).isEqualTo(List.of(hmcMiraklInvoiceOneMock, hmcMiraklInvoiceTwoMock));
	}

}
