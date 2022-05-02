package com.paypal.kyc.batchjobs.businessstakeholders;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.kyc.batchjobs.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.service.KYCReadyForReviewService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholdersDocumentsExtractBatchJobItemProcessorTest {

	public static final String FILE_PATH = "PATH";

	@InjectMocks
	private BusinessStakeholdersDocumentsExtractBatchJobItemProcessor testObj;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractServiceMock;

	@Mock
	private KYCReadyForReviewService kycReadyForReviewServiceMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelMock;

	@Mock
	private BusinessStakeholdersDocumentsExtractBatchJobItem businessStakeholdersDocumentsExtractBatchJobItemMock;

	@Mock
	private KYCDocumentModel kycDocumentModelMock;

	@Mock
	private File fileMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@RegisterExtension
	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(AbstractDocumentsBatchJobItemProcessor.class);

	@BeforeEach
	void setUp() {
		when(businessStakeholdersDocumentsExtractBatchJobItemMock.getItem())
				.thenReturn(kycDocumentBusinessStakeHolderInfoModelMock);
		when(kycDocumentBusinessStakeHolderInfoModelMock.getDocuments()).thenReturn(List.of(kycDocumentModelMock));
		when(kycDocumentModelMock.getFile()).thenReturn(fileMock);
		when(fileMock.getAbsolutePath()).thenReturn(FILE_PATH);
	}

	@Test
	void processItem_shouldPushFlagNotifyAndCleanDocuments_whenDocumentsCanBePushed() {

		when(hyperwalletBusinessStakeholderExtractServiceMock
				.pushDocuments(kycDocumentBusinessStakeHolderInfoModelMock)).thenReturn(true);

		testObj.processItem(batchJobContextMock, businessStakeholdersDocumentsExtractBatchJobItemMock);

		verify(hyperwalletBusinessStakeholderExtractServiceMock)
				.pushDocuments(kycDocumentBusinessStakeHolderInfoModelMock);
		verify(miraklBusinessStakeholderDocumentsExtractServiceMock)
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
						kycDocumentBusinessStakeHolderInfoModelMock);
		verify(kycReadyForReviewServiceMock).notifyReadyForReview(kycDocumentBusinessStakeHolderInfoModelMock);
		verify(fileMock).delete();

		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(FILE_PATH))).isTrue();
	}

	@Test
	void processItem_shouldNotNotifyAndCleanDocuments_whenDocumentsCanNotBePushed() {

		when(hyperwalletBusinessStakeholderExtractServiceMock
				.pushDocuments(kycDocumentBusinessStakeHolderInfoModelMock)).thenReturn(false);

		testObj.processItem(batchJobContextMock, businessStakeholdersDocumentsExtractBatchJobItemMock);

		verify(hyperwalletBusinessStakeholderExtractServiceMock)
				.pushDocuments(kycDocumentBusinessStakeHolderInfoModelMock);
		verify(miraklBusinessStakeholderDocumentsExtractServiceMock, times(0))
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
						kycDocumentBusinessStakeHolderInfoModelMock);
		verify(kycReadyForReviewServiceMock, times(0))
				.notifyReadyForReview(kycDocumentBusinessStakeHolderInfoModelMock);
		verify(fileMock).delete();

		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(FILE_PATH))).isTrue();
	}

}
