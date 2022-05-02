package com.paypal.kyc.batchjobs.sellers;

import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellersDocumentsExtractBatchJobItemExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private SellersDocumentsExtractBatchJobItemExtractor testObj;

	@Mock
	private MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractServiceMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2;

	@Test
	void getItems_ShouldReturnAllSellerDocumentsForAGivenDelta() {
		when(miraklSellerDocumentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(DELTA))
				.thenReturn(List.of(kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2));

		final Collection<SellersDocumentsExtractBatchJobItem> result = testObj.getItems(DELTA);

		assertThat(result.stream().map(SellersDocumentsExtractBatchJobItem::getItem).collect(Collectors.toList()))
				.containsExactlyInAnyOrder(kycDocumentSellerInfoModelMock1, kycDocumentSellerInfoModelMock2);
	}

}
