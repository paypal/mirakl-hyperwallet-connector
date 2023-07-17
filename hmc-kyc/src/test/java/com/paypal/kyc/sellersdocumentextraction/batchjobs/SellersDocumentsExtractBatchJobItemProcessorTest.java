package com.paypal.kyc.sellersdocumentextraction.batchjobs;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.kyc.documentextractioncommons.support.AbstractDocumentsBatchJobItemProcessor;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.services.KYCReadyForReviewService;
import com.paypal.kyc.sellersdocumentextraction.services.HyperwalletSellerExtractService;
import com.paypal.kyc.sellersdocumentextraction.services.MiraklSellerDocumentsExtractService;
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
class SellersDocumentsExtractBatchJobItemProcessorTest {

	public static final String FILE_PATH = "PATH";

	@InjectMocks
	private SellersDocumentsExtractBatchJobItemProcessor testObj;

	@Mock
	private MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractServiceMock;

	@Mock
	private HyperwalletSellerExtractService hyperwalletSellerExtractServiceMock;

	@Mock
	private KYCReadyForReviewService kycReadyForReviewServiceMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelMock;

	@Mock
	private SellersDocumentsExtractBatchJobItem sellersDocumentsExtractBatchJobItemMock;

	@Mock
	private KYCDocumentModel kycDocumentModelMock;

	@Mock
	private File fileMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(AbstractDocumentsBatchJobItemProcessor.class);

	@Test
	void processItem_shouldPushFlagNotifyAndCleanDocuments_whenDocumentsCanBePushed() {
		when(sellersDocumentsExtractBatchJobItemMock.getItem()).thenReturn(kycDocumentSellerInfoModelMock);
		when(kycDocumentSellerInfoModelMock.getDocuments()).thenReturn(List.of(kycDocumentModelMock));
		when(kycDocumentModelMock.getFile()).thenReturn(fileMock);
		when(fileMock.getAbsolutePath()).thenReturn(FILE_PATH);
		when(hyperwalletSellerExtractServiceMock.pushDocuments(kycDocumentSellerInfoModelMock)).thenReturn(true);

		testObj.processItem(batchJobContextMock, sellersDocumentsExtractBatchJobItemMock);
		verify(hyperwalletSellerExtractServiceMock).pushDocuments(kycDocumentSellerInfoModelMock);
		verify(miraklSellerDocumentsExtractServiceMock)
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(kycDocumentSellerInfoModelMock);
		verify(kycReadyForReviewServiceMock).notifyReadyForReview(kycDocumentSellerInfoModelMock);
		verify(fileMock).delete();

		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(FILE_PATH))).isTrue();
	}

	@Test
	void processItem_shouldNotNotifyAndCleanDocuments_whenDocumentsCanNotBePushed() {
		when(sellersDocumentsExtractBatchJobItemMock.getItem()).thenReturn(kycDocumentSellerInfoModelMock);
		when(kycDocumentSellerInfoModelMock.getDocuments()).thenReturn(List.of(kycDocumentModelMock));
		when(kycDocumentModelMock.getFile()).thenReturn(fileMock);
		when(fileMock.getAbsolutePath()).thenReturn(FILE_PATH);
		when(hyperwalletSellerExtractServiceMock.pushDocuments(kycDocumentSellerInfoModelMock)).thenReturn(false);

		testObj.processItem(batchJobContextMock, sellersDocumentsExtractBatchJobItemMock);
		verify(hyperwalletSellerExtractServiceMock).pushDocuments(kycDocumentSellerInfoModelMock);
		verify(miraklSellerDocumentsExtractServiceMock, times(0))
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(kycDocumentSellerInfoModelMock);
		verify(kycReadyForReviewServiceMock, times(0)).notifyReadyForReview(kycDocumentSellerInfoModelMock);
		verify(fileMock).delete();

		assertThat(logTrackerStub.contains("File selected to be deleted [%s]".formatted(FILE_PATH))).isTrue();
	}

}
