package com.paypal.kyc.stakeholdersdocumentextraction.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.documentextractioncommons.services.HyperwalletDocumentUploadService;
import com.paypal.kyc.stakeholdersdocumentextraction.services.converters.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder.VerificationStatus.REQUIRED;
import static com.hyperwallet.clientsdk.model.HyperwalletBusinessStakeholder.VerificationStatus.VERIFIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperwalletBusinessStakeholderExtractServiceImplTest {

	private static final String USER_TOKEN = "userToken";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String BUSINESS_STAKE_HOLDER_ONE_TOKEN = "businessStakeHolderOneToken";

	@Spy
	@InjectMocks
	private HyperwalletBusinessStakeholderExtractServiceImpl testObj;

	@Mock
	private UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	private Hyperwallet hyperwalletApiClientMock;

	@Mock
	private KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock;

	@Mock
	private HyperwalletDocumentUploadService hyperwalletDocumentUploadServiceMock;

	@Mock
	private HyperwalletList<HyperwalletBusinessStakeholder> businessStakeholders;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModelMock;

	@Mock
	private HyperwalletVerificationDocument usrOneBstOneFilesOneDataMock;

	@Mock
	private HyperwalletBusinessStakeholder businessStakeHolderRequiredVerificationMock, businessStakeHolderVerifiedMock;

	@Test
	void getKYCRequiredVerificationBusinessStakeHolders_whenNoBusinessStakeholdersAreReceived_shouldReturnEmptyList() {
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<String> result = testObj.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM,
				USER_TOKEN);

		verify(userHyperwalletSDKServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		assertThat(result).isEmpty();
	}

	@Test
	void getKYCRequiredVerificationBusinessStakeHolders_whenNoDataIsReceived_shouldReturnEmptyList() {
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);
		when(hyperwalletApiClientMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(businessStakeholders);
		when(businessStakeholders.getData()).thenReturn(Collections.emptyList());

		final List<String> result = testObj.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM,
				USER_TOKEN);

		verify(userHyperwalletSDKServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		assertThat(result).isEmpty();
	}

	@Test
	void getKYCRequiredVerificationBusinessStakeHolders_whenNoRequiredVerificationBusinessStakeholdersIsReceived_shouldReturnEmptyList() {
		when(businessStakeHolderRequiredVerificationMock.getVerificationStatus()).thenReturn(VERIFIED);
		when(businessStakeholders.getData()).thenReturn(List.of(businessStakeHolderRequiredVerificationMock));
		when(hyperwalletApiClientMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(businessStakeholders);
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<String> result = testObj.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM,
				USER_TOKEN);

		verify(userHyperwalletSDKServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		assertThat(result).isEmpty();
	}

	@Test
	void getKYCRequiredVerificationBusinessStakeHolders_shouldReturnRequiredVerificationBusinessStakeHolders() {
		when(businessStakeHolderRequiredVerificationMock.getToken()).thenReturn(BUSINESS_STAKE_HOLDER_ONE_TOKEN);
		when(businessStakeHolderRequiredVerificationMock.getVerificationStatus()).thenReturn(REQUIRED);
		when(businessStakeHolderVerifiedMock.getVerificationStatus()).thenReturn(VERIFIED);
		when(businessStakeholders.getData())
				.thenReturn(List.of(businessStakeHolderRequiredVerificationMock, businessStakeHolderVerifiedMock));
		when(hyperwalletApiClientMock.listBusinessStakeholders(USER_TOKEN)).thenReturn(businessStakeholders);
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM))
				.thenReturn(hyperwalletApiClientMock);

		final List<String> result = testObj.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM,
				USER_TOKEN);

		verify(userHyperwalletSDKServiceMock).getHyperwalletInstanceByHyperwalletProgram(HYPERWALLET_PROGRAM);
		assertThat(result).containsExactlyInAnyOrder(BUSINESS_STAKE_HOLDER_ONE_TOKEN);
	}

	@Test
	void pushDocuments_whenAllDocumentsAreFulfilledByBusinessStakeholder_shouldPushDocumentsForBusinessStakeholder() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.areDocumentsFilled()).thenReturn(true);

		final List<HyperwalletVerificationDocument> usrOneBstOneFilesOneDataList = List
				.of(usrOneBstOneFilesOneDataMock);
		when(businessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutorMock
				.execute(kycDocumentBusinessStakeHolderInfoModelMock)).thenReturn(usrOneBstOneFilesOneDataList);
		testObj.pushDocuments(kycDocumentBusinessStakeHolderInfoModelMock);

		verify(hyperwalletDocumentUploadServiceMock).uploadDocument(kycDocumentBusinessStakeHolderInfoModelMock,
				usrOneBstOneFilesOneDataList);
	}

	@Test
	void pushDocuments_shouldSkipBusinessStakeholdersWithNotAllDocumentsFulfilled() {
		when(kycDocumentBusinessStakeHolderInfoModelMock.areDocumentsFilled()).thenReturn(false);
		testObj.pushDocuments(kycDocumentBusinessStakeHolderInfoModelMock);

		verify(hyperwalletDocumentUploadServiceMock, times(0)).uploadDocument(any(), any());
	}

}
