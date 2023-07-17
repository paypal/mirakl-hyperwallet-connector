package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentsExtractionResult;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJobItem;
import com.paypal.kyc.sellersdocumentextraction.batchjobs.SellersDocumentsExtractBatchJobItemExtractor;
import com.paypal.kyc.sellersdocumentextraction.services.MiraklSellerDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellersDocumentsExtractBatchJobItemExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private SellersDocumentsExtractBatchJobItemExtractor testObj;

	@Mock
	private MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractServiceMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2;

	@Mock
	private KYCDocumentsExtractionResult<KYCDocumentSellerInfoModel> kycDocumentsExtractionResultMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnAllSellerDocumentsForAGivenDelta_WhenNoPartialErrorsHappened() {
		when(miraklSellerDocumentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(DELTA))
				.thenReturn(kycDocumentsExtractionResultMock);
		when(kycDocumentsExtractionResultMock.getExtractedDocuments())
				.thenReturn(List.of(kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2));

		final Collection<SellersDocumentsExtractBatchJobItem> result = testObj.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(SellersDocumentsExtractBatchJobItem::getItem).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(false);
	}

	@Test
	void getItems_ShouldReturnNotFailedSellerDocumentsForAGivenDeltaAndSetAPartialExtraction_WhenPartialErrorsHappened() {
		when(miraklSellerDocumentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(DELTA))
				.thenReturn(kycDocumentsExtractionResultMock);
		when(kycDocumentsExtractionResultMock.getExtractedDocuments())
				.thenReturn(List.of(kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2));
		when(kycDocumentsExtractionResultMock.hasFailed()).thenReturn(true);

		final Collection<SellersDocumentsExtractBatchJobItem> result = testObj.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(SellersDocumentsExtractBatchJobItem::getItem).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(true);
	}

}
