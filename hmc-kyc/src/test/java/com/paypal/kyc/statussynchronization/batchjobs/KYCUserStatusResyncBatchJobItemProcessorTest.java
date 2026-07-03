package com.paypal.kyc.statussynchronization.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.services.KYCUserDocumentFlagsExecutor;
import com.paypal.kyc.incomingnotifications.services.KYCUserStatusExecutor;
import com.paypal.kyc.statussynchronization.model.KYCUserStatusInfoModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
	private KYCUserStatusInfoModel kycUserStatusInfoModelMock;

	@Mock
	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModelMock;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel kYCUserDocumentFlagsNotificationBodyModelMock;

	@Test
	void processItem_shouldDelegateToKYCUserStatusExecutorAndKYCUserDocumentFlagsExecutor() {
		// given
		final KYCUserStatusResyncBatchJobItem kycUserStatusResyncBatchJobItem = new KYCUserStatusResyncBatchJobItem(
				kycUserStatusInfoModelMock);
		when(kycUserStatusInfoModelMock.getKycUserStatusNotificationBodyModel())
			.thenReturn(kycUserStatusNotificationBodyModelMock);
		when(kycUserStatusInfoModelMock.getKycUserDocumentFlagsNotificationBodyModel())
			.thenReturn(kYCUserDocumentFlagsNotificationBodyModelMock);

		// when
		testObj.processItem(mock(BatchJobContext.class), kycUserStatusResyncBatchJobItem);

		// then
		verify(kyCUserStatusExecutorMock, times(1)).execute(kycUserStatusNotificationBodyModelMock);
		verify(kycUserDocumentFlagsExecutorMock, times(1)).execute(kYCUserDocumentFlagsNotificationBodyModelMock);
	}

}
