package com.paypal.kyc.jobs;

import com.paypal.infrastructure.repository.JobExecutionInformationRepository;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.DocumentsExtractService;
import com.paypal.kyc.service.KYCReadyForReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentsExtractJobTest {

	private static final String DELTA = "delta";

	private static final String JOB_NAME = "jobName";

	@InjectMocks
	private DocumentsExtractJob testObj;

	@Mock
	private DocumentsExtractService documentsExtractServiceMock;

	@Mock
	private KYCReadyForReviewService KYCReadyForReviewServiceMock;

	@Mock
	private JobExecutionInformationRepository jobExecutionInformationRepositoryMock;

	@Mock
	private JobExecutionContext contextMock;

	@Mock
	private KYCDocumentSellerInfoModel documentSellerInfoModelMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel documentBusinessStakeHolderInfoModelMock;

	@Captor
	private ArgumentCaptor<Date> dateArgumentCaptor;

	@Captor
	private ArgumentCaptor<List<KYCDocumentInfoModel>> documentInfoCaptor;

	@Test
	void execute_whenNoDeltaKeyIsFound_shouldCallDocumentsExtractServiceWithDeltaAsNull_andNotifyDocumentsSent_andCleanUpDocuments() {
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class).withIdentity(JOB_NAME).build());
		when(documentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(null))
				.thenReturn(Collections.singletonList(documentSellerInfoModelMock));
		when(documentsExtractServiceMock.extractBusinessStakeholderDocuments(null))
				.thenReturn(Collections.singletonList(documentBusinessStakeHolderInfoModelMock));

		testObj.execute(contextMock);

		final InOrder inOrder = inOrder(documentsExtractServiceMock, KYCReadyForReviewServiceMock);

		inOrder.verify(documentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(null);
		inOrder.verify(documentsExtractServiceMock).extractBusinessStakeholderDocuments(null);
		inOrder.verify(KYCReadyForReviewServiceMock).notifyReadyForReview(documentInfoCaptor.capture());
		final List<KYCDocumentInfoModel> documents = documentInfoCaptor.getValue();
		assertThat(documents).containsExactlyInAnyOrder(documentSellerInfoModelMock,
				documentBusinessStakeHolderInfoModelMock);
		inOrder.verify(documentsExtractServiceMock).cleanUpDocumentsFiles(documents);
	}

	@Test
	void execute_whenDeltaKeyIsFound_shouldCallMiraklDocumentsExtractServiceWithDeltaAsNull_andNotifyDocumentsSent_andCleanUpDocuments() {
		final Date expectedDate = new Date();
		when(contextMock.getJobDetail()).thenReturn(JobBuilder.newJob(MyJob.class)
				.usingJobData(new JobDataMap(Map.of(DELTA, expectedDate))).withIdentity(JOB_NAME).build());
		when(documentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(expectedDate))
				.thenReturn(Collections.singletonList(documentSellerInfoModelMock));
		when(documentsExtractServiceMock.extractBusinessStakeholderDocuments(expectedDate))
				.thenReturn(Collections.singletonList(documentBusinessStakeHolderInfoModelMock));

		testObj.execute(contextMock);

		verify(documentsExtractServiceMock)
				.extractProofOfIdentityAndBusinessSellerDocuments(dateArgumentCaptor.capture());
		assertThat(dateArgumentCaptor.getValue()).isEqualTo(expectedDate);
		verify(documentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(expectedDate);

		final InOrder inOrder = inOrder(documentsExtractServiceMock, KYCReadyForReviewServiceMock);

		inOrder.verify(documentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(expectedDate);
		inOrder.verify(documentsExtractServiceMock).extractBusinessStakeholderDocuments(expectedDate);
		inOrder.verify(KYCReadyForReviewServiceMock).notifyReadyForReview(documentInfoCaptor.capture());
		final List<KYCDocumentInfoModel> documents = documentInfoCaptor.getValue();
		assertThat(documents).containsExactlyInAnyOrder(documentSellerInfoModelMock,
				documentBusinessStakeHolderInfoModelMock);
		inOrder.verify(documentsExtractServiceMock).cleanUpDocumentsFiles(documents);
	}

	@Test
	void createJobDataMap_shouldReturnMapWithDateAsDeltaValue() {
		final Date expectedDate = new Date();

		final JobDataMap result = DocumentsExtractJob.createJobDataMap(expectedDate);

		assertThat(result).containsEntry(DELTA, expectedDate);
	}

	private static class MyJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) {
			// doNothing
		}

	}

}
