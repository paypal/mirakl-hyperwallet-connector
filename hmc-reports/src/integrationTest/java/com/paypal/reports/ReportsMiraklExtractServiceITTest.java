package com.paypal.reports;

import com.paypal.reports.model.HmcMiraklTransactionLine;
import com.paypal.reports.services.ReportsMiraklExtractService;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportsMiraklExtractServiceITTest extends AbstractMockEnabledIntegrationTest {

	@Autowired
	private ReportsMiraklExtractService testObj;

	@Test
	void getAllTransactionLinesByDate_shouldReturnAllTransactionLinesWithCorrectFieldMapping_whenSinglePage() {
		miraklTransactionLinesEndpointMock.getTransactionLines("transaction-lines-single-page.json");

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(new Date(), new Date());

		assertThat(result).hasSize(5);

		final HmcMiraklTransactionLine first = result.get(0);
		assertThat(first.getTransactionLineId()).isEqualTo("TXN001");
		assertThat(first.getTransactionType()).isEqualTo("ORDER_AMOUNT");
		assertThat(first.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
		assertThat(first.getCreditAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
		assertThat(first.getDebitAmount()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(first.getCurrencyIsoCode()).isEqualTo("EUR");
		assertThat(first.getTransactionTime()).isNotNull();
		assertThat(first.getSellerId()).isEqualTo("2001");
		assertThat(first.getOrderId()).isEqualTo("order-001");
		assertThat(first.getTransactionNumber()).isEqualTo("TN001");
	}

	@Test
	void getAllTransactionLinesByDate_shouldFetchAllPages_whenNextPageTokenIsPresent() {
		miraklTransactionLinesEndpointMock.getTransactionLinesWithPageToken("next_token",
				"transaction-lines-page-2.json");
		miraklTransactionLinesEndpointMock.getTransactionLines("transaction-lines-page-1.json");

		final List<HmcMiraklTransactionLine> result = testObj.getAllTransactionLinesByDate(new Date(), new Date());

		assertThat(result).hasSize(5);
		assertThat(result).extracting(HmcMiraklTransactionLine::getTransactionLineId)
			.containsExactly("TXN001", "TXN002", "TXN003", "TXN004", "TXN005");
	}

}
