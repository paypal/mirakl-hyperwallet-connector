package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.services.KYCUserDocumentFlagsExecutor;
import com.paypal.kyc.incomingnotifications.services.KYCUserStatusExecutor;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCUserStatusResyncBatchJobItemProcessorTest {

	@InjectMocks
	private KYCUserStatusResyncBatchJobItemProcessor testObj;

	@Mock
	private KYCUserStatusExecutor kyCUserStatusExecutorMock;

	@Mock
	private KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutorMock;

	@Mock
	private KYCUserStatusInfoModel kYCUserStatusInfoModelMock;

	@Mock
	private KYCUserStatusNotificationBodyModel kYCUserStatusNotificationBodyModelMock;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel kYCUserDocumentFlagsNotificationBodyModelMock;

	@Test
	void processItem_shouldDelegateToKYCUserStatusExecutorAndKYCUserDocumentFlagsExecutor() {
		// given
		final KYCUserStatusResyncBatchJobItem KYCUserStatusResyncBatchJobItem = new KYCUserStatusResyncBatchJobItem(
				kYCUserStatusInfoModelMock);
		when(kYCUserStatusInfoModelMock.getKycUserStatusNotificationBodyModel())
				.thenReturn(kYCUserStatusNotificationBodyModelMock);
		when(kYCUserStatusInfoModelMock.getKycUserDocumentFlagsNotificationBodyModel())
				.thenReturn(kYCUserDocumentFlagsNotificationBodyModelMock);

		// when
		testObj.processItem(mock(BatchJobContext.class), KYCUserStatusResyncBatchJobItem);

		// then
		verify(kyCUserStatusExecutorMock, times(1)).execute(kYCUserStatusNotificationBodyModelMock);
		verify(kycUserDocumentFlagsExecutorMock, times(1)).execute(kYCUserDocumentFlagsNotificationBodyModelMock);
	}

}
