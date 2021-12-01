package com.paypal.kyc.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsExtractServiceImplTest {

	@InjectMocks
	private DocumentsExtractServiceImpl testObj;

	@Mock
	private HyperwalletSellerExtractService hyperwalletSellerExtractServiceMock;

	@Mock
	private MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractServiceMock;

	@Mock
	private HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractServiceMock;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private Date deltaMock;

	@Mock
	private File fileOneMock, fileTwoMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOneMock, kycDocumentSellerInfoModelEmptyMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelOneMock,
			kycDocumentBusinessStakeHolderInfoModelEmptyMock;

	@RegisterExtension
	LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.INFO)
			.recordForType(DocumentsExtractServiceImpl.class);

	@Test
	void extractProofOfIdentityAndBusinessSellerDocuments_shouldRetrieveAllKYCSellerInfoModelFromMiraklAndPushThemIntoHyperwalletAndSetDocumentFlagToFalseBasedOnPushDocumentsResponse() {
		final List<KYCDocumentSellerInfoModel> kycDocumentSellerInfoModelList = List
				.of(kycDocumentSellerInfoModelOneMock, kycDocumentSellerInfoModelEmptyMock);
		when(miraklSellerDocumentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock))
				.thenReturn(kycDocumentSellerInfoModelList);
		final List<KYCDocumentSellerInfoModel> successFullPushedListOfDocuments = List
				.of(kycDocumentSellerInfoModelOneMock);
		when(hyperwalletSellerExtractServiceMock
				.pushProofOfIdentityAndBusinessSellerDocuments(kycDocumentSellerInfoModelList))
						.thenReturn(successFullPushedListOfDocuments);

		testObj.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);

		verify(miraklSellerDocumentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);
		verify(hyperwalletSellerExtractServiceMock)
				.pushProofOfIdentityAndBusinessSellerDocuments(kycDocumentSellerInfoModelList);
		verify(miraklSellerDocumentsExtractServiceMock)
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(successFullPushedListOfDocuments);
	}

	@Test
	void extractBusinessStakeholderDocuments_shouldRetrieveAllKYCBusinessStakeholderInfoModelFromMiraklAndSetDocumentFlagToFalseBasedOnPushDocumentsResponse() {
		final List<KYCDocumentBusinessStakeHolderInfoModel> kycDocumentBusinessStakeholderInfoModelList = List
				.of(kycDocumentBusinessStakeHolderInfoModelOneMock, kycDocumentBusinessStakeHolderInfoModelEmptyMock);
		when(miraklBusinessStakeholderDocumentsExtractServiceMock.extractBusinessStakeholderDocuments(deltaMock))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelList);
		final List<KYCDocumentBusinessStakeHolderInfoModel> successFullPushedListOfDocuments = List
				.of(kycDocumentBusinessStakeHolderInfoModelOneMock);
		when(hyperwalletBusinessStakeholderExtractServiceMock
				.pushBusinessStakeholderDocuments(kycDocumentBusinessStakeholderInfoModelList))
						.thenReturn(successFullPushedListOfDocuments);

		testObj.extractBusinessStakeholderDocuments(deltaMock);

		verify(miraklBusinessStakeholderDocumentsExtractServiceMock).extractBusinessStakeholderDocuments(deltaMock);
		verify(hyperwalletBusinessStakeholderExtractServiceMock)
				.pushBusinessStakeholderDocuments(kycDocumentBusinessStakeholderInfoModelList);
		verify(miraklBusinessStakeholderDocumentsExtractServiceMock)
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
						successFullPushedListOfDocuments);
	}

	@Test
	void cleanUpFiles_shouldRemoveFilesReceivedAsParameter() {

		final KYCDocumentModel kycDocumentModelOne = KYCDocumentModel.builder().file(fileOneMock).build();
		final KYCDocumentModel kycDocumentModelTwo = KYCDocumentModel.builder().file(fileTwoMock).build();
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder().clientUserId("2000")
				.documents(List.of(kycDocumentModelOne, kycDocumentModelTwo)).build();
		final List<KYCDocumentSellerInfoModel> successfullyPushedDocumentsList = List.of(kycDocumentOne);

		testObj.cleanUpDocumentsFiles(successfullyPushedDocumentsList);

		verify(fileOneMock).delete();
		verify(fileTwoMock).delete();
		assertThat(logTrackerStub.contains("Cleaning up done successfully!")).isTrue();
	}

	@Test
	void cleanUpFiles_shouldNotDoAnythingWhenNullParameterIsReceived() {

		testObj.cleanUpDocumentsFiles(null);
		assertThat(logTrackerStub.contains("Cleaning up done successfully!")).isTrue();
	}

}
