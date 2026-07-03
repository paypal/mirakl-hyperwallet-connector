package com.paypal.invoices;

import com.mirakl.client.mmp.domain.payment.sellerbillingcycle.MiraklPayOutState;
import com.paypal.invoices.extractioninvoices.model.InvoiceModel;
import com.paypal.invoices.extractioninvoices.services.MiraklInvoicesExtractServiceImpl;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MiraklInvoicesExtractServiceITTest extends AbstractMockEnabledIntegrationTest {

	private static final UUID EXPECTED_ID_1 = UUID.fromString("a5b77ee5-f5c2-4efc-ab94-8366ee781598");

	private static final String SBC_11_MULTIPLE_RESPONSE_JSON = "sbc11-multiple-response.json";

	@Autowired
	private MiraklInvoicesExtractServiceImpl testObj;

	@Test
	void shouldExtractInvoices_withoutPaidOnes() {
		final Instant startDate = Instant.parse("2014-01-01T00:00:00Z");
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCyclesWithStartDate(startDate.toString(),
				MiraklPayOutState.TO_PAY.toString(), SBC_11_MULTIPLE_RESPONSE_JSON);

		final Date delta = Date.from(startDate);
		final List<InvoiceModel> invoices = testObj.extractAccountingDocuments(delta);

		assertThat(invoices).isNotEmpty().allMatch(inv -> inv.getInvoiceNumber() != null);
	}

	@Test
	void shouldExtractInvoices_includingPaid() {
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCycles(SBC_11_MULTIPLE_RESPONSE_JSON);

		final Date delta = Date.from(Instant.parse("2014-01-01T00:00:00Z"));
		final List<InvoiceModel> invoices = testObj.extractAccountingDocuments(delta, true);

		assertThat(invoices).isNotEmpty().allMatch(inv -> inv.getInvoiceNumber() != null);
	}

	@Test
	void shouldExtractInvoices_whenDateIsRecent() {
		final Instant now = LocalDateTime.of(2026, 6, 22, 0, 0).toInstant(ZoneOffset.UTC);
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCyclesWithStartDate(now.toString(),
				MiraklPayOutState.TO_PAY.toString(), SBC_11_MULTIPLE_RESPONSE_JSON);

		final Date delta = Date.from(now);
		final List<InvoiceModel> invoices = testObj.extractAccountingDocuments(delta);

		assertThat(invoices).isNotEmpty();
	}

	@Test
	void shouldExtractInvoicesById() {
		miraklSellerBillingCyclesEndpointMock.getSellerBillingCycles(SBC_11_MULTIPLE_RESPONSE_JSON);

		final List<InvoiceModel> invoices = (List<InvoiceModel>) testObj
			.extractAccountingDocuments(List.of(EXPECTED_ID_1.toString()));

		assertThat(invoices).hasSize(1);
		assertThat(invoices.getFirst().getInvoiceNumber()).isEqualTo(EXPECTED_ID_1.toString());
	}

}
