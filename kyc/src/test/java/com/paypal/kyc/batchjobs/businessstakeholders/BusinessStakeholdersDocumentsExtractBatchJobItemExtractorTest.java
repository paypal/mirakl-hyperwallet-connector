package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
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
import static org.mockito.Mockito.when;

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

	@Test
	void getItems_ShouldReturnAllBusinessStakeholderDocumentsForAGivenDelta() {
		when(miraklBusinessStakeholderDocumentsExtractServiceMock.extractBusinessStakeholderDocuments(DELTA))
				.thenReturn(List.of(kycDocumentBusinessStakeHolderInfoModelMock1,
						kycDocumentBusinessStakeHolderInfoModelMock2));

		final Collection<BusinessStakeholdersDocumentsExtractBatchJobItem> result = testObj.getItems(DELTA);

		assertThat(result.stream().map(BusinessStakeholdersDocumentsExtractBatchJobItem::getItem)
				.collect(Collectors.toList())).containsExactlyInAnyOrder(kycDocumentBusinessStakeHolderInfoModelMock1,
						kycDocumentBusinessStakeHolderInfoModelMock2);
	}

}
