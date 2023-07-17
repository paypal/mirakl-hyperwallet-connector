package com.paypal.kyc;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItem;
import com.paypal.kyc.stakeholdersdocumentextraction.batchjobs.BusinessStakeholdersDocumentsExtractBatchJobItemExtractor;
import com.paypal.testsupport.AbstractMockEnabledIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BusinessStakeholdersDocumentsExtractBatchJobItemExtractorITTest extends AbstractMockEnabledIntegrationTest {

	@Autowired
	private BusinessStakeholdersDocumentsExtractBatchJobItemExtractor testObj;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnAllBusinessStakeholderDocumentsForAGivenDelta_WhenNoPartialErrorsHappened() {
		final Date delta = new Date();
		miraklShopsEndpointMock.getShops(delta, false, "get-shops-bstk.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("1", "get-shops-documents-1.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("2", "get-shops-documents-2.json");
		miraklShopsDocumentsEndpointMock.getShopDocument("1001", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("1002", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("1003", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("1004", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("2001", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("2002", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("2003", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("2004", "get-shops-document.png");

		final Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> result = invokeGetItems(delta);

		assertThat(result.stream().map(BusinessStakeholdersDocumentsExtractBatchJobItem::getItem)
				.collect(Collectors.toList())).hasSize(4);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(false);
		verify(batchJobContextMock, times(1)).setNumberOfItemsNotSuccessfullyExtracted(0);
	}

	@Test
	void getItems_ShouldReturnNotFailedBusinessStakeholderDocumentsForAGivenDeltaAndSetAPartialExtraction_WhenPartialErrorsHappened() {
		final Date delta = new Date();
		miraklShopsEndpointMock.getShops(delta, false, "get-shops-bstk.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("1", "get-shops-documents-1.json");
		miraklShopsDocumentsEndpointMock.getShopDocuments("2", "get-shops-documents-2.json");
		miraklShopsDocumentsEndpointMock.getShopDocument("1001", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("1002", "get-shops-document.png");
		miraklShopsDocumentsEndpointMock.getShopDocument("1003", "get-shops-document.png");

		final Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> result = invokeGetItems(delta);

		assertThat(result.stream().map(BusinessStakeholdersDocumentsExtractBatchJobItem::getItem)
				.collect(Collectors.toList())).hasSize(1);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(true);
		verify(batchJobContextMock, times(1)).setNumberOfItemsNotSuccessfullyExtracted(3);

	}

	private Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> invokeGetItems(final Date delta) {
		return ReflectionTestUtils.invokeMethod(testObj, "getItems", batchJobContextMock, delta);
	}

}
