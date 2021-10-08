package com.paypal.infrastructure.sdk.mirakl.domain.invoice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCMiraklInvoiceTest {

	@Mock
	private HMCMiraklInvoiceSummary hmcMiraklInvoiceSummaryMock;

	@Test
	void getHMCInvoices_returnsWhatItWasSet() {
		final HMCMiraklInvoice testObj = new HMCMiraklInvoice();
		testObj.setSummary(hmcMiraklInvoiceSummaryMock);

		final HMCMiraklInvoiceSummary result = testObj.getSummary();

		assertThat(result).isEqualTo(hmcMiraklInvoiceSummaryMock);
	}

}
