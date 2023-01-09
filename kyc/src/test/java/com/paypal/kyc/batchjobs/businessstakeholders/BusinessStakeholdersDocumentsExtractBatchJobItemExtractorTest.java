package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentsExtractionResult;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
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
class BusinessStakeholdersDocumentsExtractBatchJobItemExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private BusinessStakeholdersDocumentsExtractBatchJobItemExtractor testObj;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelMock1,
			kycDocumentBusinessStakeHolderInfoModelMock2;

	@Mock
	private KYCDocumentsExtractionResult<KYCDocumentBusinessStakeHolderInfoModel> kycDocumentsExtractionResultMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void getItems_ShouldReturnAllBusinessStakeholderDocumentsForAGivenDelta_WhenNoPartialErrorsHappened() {
		when(miraklBusinessStakeholderDocumentsExtractServiceMock.extractBusinessStakeholderDocuments(DELTA))
				.thenReturn(kycDocumentsExtractionResultMock);
		when(kycDocumentsExtractionResultMock.getExtractedDocuments()).thenReturn(
				List.of(kycDocumentBusinessStakeHolderInfoModelMock1, kycDocumentBusinessStakeHolderInfoModelMock2));

		final Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> result = testObj
				.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(BusinessStakeholdersDocumentsExtractBatchJobItem::getItem)
				.collect(Collectors.toList())).containsExactlyInAnyOrder(kycDocumentBusinessStakeHolderInfoModelMock1,
						kycDocumentBusinessStakeHolderInfoModelMock2);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(false);
	}

	@Test
	void getItems_ShouldReturnNotFailedBusinessStakeholderDocumentsForAGivenDeltaAndSetAPartialExtraction_WhenPartialErrorsHappened() {
		when(miraklBusinessStakeholderDocumentsExtractServiceMock.extractBusinessStakeholderDocuments(DELTA))
				.thenReturn(kycDocumentsExtractionResultMock);
		when(kycDocumentsExtractionResultMock.getExtractedDocuments()).thenReturn(
				List.of(kycDocumentBusinessStakeHolderInfoModelMock1, kycDocumentBusinessStakeHolderInfoModelMock2));
		when(kycDocumentsExtractionResultMock.hasFailed()).thenReturn(true);

		final Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> result = testObj
				.getItems(batchJobContextMock, DELTA);

		assertThat(result.stream().map(BusinessStakeholdersDocumentsExtractBatchJobItem::getItem)
				.collect(Collectors.toList())).containsExactlyInAnyOrder(kycDocumentBusinessStakeHolderInfoModelMock1,
						kycDocumentBusinessStakeHolderInfoModelMock2);

		verify(batchJobContextMock, times(1)).setPartialItemExtraction(true);
	}

}
