package com.paypal.kyc.batchjobs.sellers;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.kyc.KycIntegrationTests;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SellersDocumentsExtractBatchJobItemExtractorITTest extends KycIntegrationTests {

	@Autowired
	private SellersDocumentsExtractBatchJobItemExtractor testObj;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnAllSellerDocumentsForAGivenDelta_WhenNoPartialErrorsHappened() {
		final Date delta = new Date();
		miraklShopsEndpointMock.getShops(delta, false, "get-shops-sellers.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("1", "get-shops-documents-1.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("2", "get-shops-documents-2.json");
		miraklShopsDocumentsEndpointMock.getShopDocument("1005", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("2005", "get-shops-document.png");

		final Collection<SellersDocumentsExtractBatchJobItem> result = testObj.getItems(batchJobContextMock, delta);

		assertThat(result.stream().map(SellersDocumentsExtractBatchJobItem::getItem).collect(Collectors.toList()))
				.hasSize(2);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(false);
		verify(batchJobContextMock, times(1)).setNumberOfItemsNotSuccessfullyExtracted(0);
	}

	@Tag("noParallel")
	@Test
	void getItems_ShouldReturnNotFailedSellerDocumentsForAGivenDeltaAndSetAPartialExtraction_WhenPartialErrorsHappened() {
		final Date delta = new Date();
		miraklShopsEndpointMock.getShops(delta, false, "get-shops-sellers.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("1", "get-shops-documents-1.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("2", "get-shops-documents-2.json");
		miraklShopsDocumentsEndpointMock.getShopDocument("1005", "get-shops-document.png");

		final Collection<SellersDocumentsExtractBatchJobItem> result = testObj.getItems(batchJobContextMock, delta);

		assertThat(result.stream().map(SellersDocumentsExtractBatchJobItem::getItem).collect(Collectors.toList()))
				.hasSize(1);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(true);
		verify(batchJobContextMock, times(1)).setNumberOfItemsNotSuccessfullyExtracted(1);
	}

}
