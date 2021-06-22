package com.paypal.kyc.service.impl;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.HMCDocumentsExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsExtractServiceImplTest {

	@InjectMocks
	private DocumentsExtractServiceImpl testObj;

	@Mock
	private Date deltaMock;

	@Mock
	private MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractServiceMock;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModelOneMock, kycDocumentSellerInfoModelEmptyMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelOneMock,
			kycDocumentBusinessStakeHolderInfoModelEmptyMock;

	@Mock
	private HyperwalletSellerExtractService hyperwalletSellerExtractServiceMock;

	@Mock
	private HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractServiceMock;

	@Mock
	private HMCDocumentsExtractService hmcDocumentsExtractServiceMock;

	@Test
	void extractProofOfIdentityAndBusinessSellerDocuments_shouldRetrieveAllKYCSellerInfoModelFromMiraklAndPushThemIntoHyperwalletAndSetDocumentFlagToFalseBasedOnPushDocumentsResponse() {
		final List<KYCDocumentSellerInfoModel> kycDocumentSellerInfoModelList = List
				.of(this.kycDocumentSellerInfoModelOneMock, kycDocumentSellerInfoModelEmptyMock);
		when(miraklSellerDocumentsExtractServiceMock.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock))
				.thenReturn(kycDocumentSellerInfoModelList);
		final List<KYCDocumentSellerInfoModel> successFullPushedListOfDocuments = List
				.of(this.kycDocumentSellerInfoModelOneMock);
		when(hyperwalletSellerExtractServiceMock
				.pushProofOfIdentityAndBusinessSellerDocuments(kycDocumentSellerInfoModelList))
						.thenReturn(successFullPushedListOfDocuments);

		testObj.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);

		verify(miraklSellerDocumentsExtractServiceMock).extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);
		verify(hyperwalletSellerExtractServiceMock)
				.pushProofOfIdentityAndBusinessSellerDocuments(kycDocumentSellerInfoModelList);
		verify(miraklSellerDocumentsExtractServiceMock)
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(successFullPushedListOfDocuments);
		verify(hmcDocumentsExtractServiceMock).cleanUpDocumentsFiles(successFullPushedListOfDocuments);
	}

	@Test
	void extractBusinessStakeholderDocuments_shouldRetrieveAllKYCBusinessStakeholderInfoModelFromMiraklAndPushThemIntoHyperwalletAndSetDocumentFlagToFalseBasedOnPushDocumentsResponse() {
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
		verify(hmcDocumentsExtractServiceMock).cleanUpDocumentsFiles(successFullPushedListOfDocuments);
	}

}
