package com.paypal.infrastructure.sdk.mirakl.domain.invoice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HMCMiraklInvoiceSummaryTest {

	@Test
	void setAmountTransferredToOperator() {
		final BigDecimal amountTransferredToOperator = new BigDecimal(10);
		final HMCMiraklInvoiceSummary testObj = new HMCMiraklInvoiceSummary();

		testObj.setAmountTransferredToOperator(amountTransferredToOperator);

		assertThat(testObj.getAmountTransferredToOperator()).isEqualTo(amountTransferredToOperator);
	}

}
