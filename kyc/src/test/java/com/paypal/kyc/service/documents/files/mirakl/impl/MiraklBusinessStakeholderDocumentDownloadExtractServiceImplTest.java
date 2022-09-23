package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.strategies.documents.files.mirakl.impl.MiraklKYCSelectionDocumentExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklBusinessStakeholderDocumentDownloadExtractServiceImplTest {

	private static final String MIRAKL_SHOP_ID = "2000";

	@Spy
	@InjectMocks
	private MiraklBusinessStakeholderDocumentDownloadExtractServiceImpl testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private MiraklKYCSelectionDocumentExecutor proofOfIdentityStrategyExecutorMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Mock
	private static KYCDocumentBusinessStakeHolderInfoModel KYCDocumentBusinessStakeHolderInfoModelMock;

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldReturnAnEmptyListWhenNoProofOfAddressNeitherProofOfIdentityHasBeenSelectedBySeller() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycBusinessStakeholderNonSelectedDocuments = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycBusinessStakeholderNonSelectedDocuments);

		assertThat(result.getDocuments()).isEmpty();
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldPopulateKYCInfoModelWithDocumentInformationAndReturnDocumentsReturnedByStrategies() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.businessStakeholderMiraklNumber(1)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on

		final KYCDocumentModel kycDocumentModelFront = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentModel kycDocumentModelBack = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK).build();

		final MiraklShopDocument miraklShopProofOfIdentityDocumentFront = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentFront.setTypeCode("hw-bsh1-proof-identity-front");
		final MiraklShopDocument miraklShopProofOfIdentityDocumentBack = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentBack.setTypeCode("hw-bsh1-proof-identity-back");

		final List<MiraklShopDocument> miraklShopDocumentsList = List.of(miraklShopProofOfIdentityDocumentFront,
				miraklShopProofOfIdentityDocumentBack);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID))))
						.thenReturn(miraklShopDocumentsList);

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModelWithMiraklDocumentsShopInformation = kycDocumentBusinessStakeHolderInfoModel
				.toBuilder().miraklShopDocuments(miraklShopDocumentsList).build();

		when(proofOfIdentityStrategyExecutorMock
				.execute(kycDocumentBusinessStakeholderInfoModelWithMiraklDocumentsShopInformation))
						.thenReturn(List.of(List.of(kycDocumentModelFront, kycDocumentModelBack)));

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID)));
		verifyNoMoreInteractions(miraklMarketplacePlatformOperatorApiClientMock);

		assertThat(result.getDocuments()).containsExactlyInAnyOrder(kycDocumentModelFront, kycDocumentModelBack);
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldSendMailNotificationWhenMiraklExceptionIsThrown() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.businessStakeholderMiraklNumber(1)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on

		final MiraklShopDocument miraklShopProofOfIdentityDocumentFront = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentFront.setTypeCode("hw-bsh1-proof-identity-front");
		final MiraklShopDocument miraklShopProofOfIdentityDocumentBack = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentBack.setTypeCode("hw-bsh1-proof-identity-back");

		final MiraklException miraklException = new MiraklException("Something wrong happened");
		doThrow(miraklException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID)));

		testObj.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		verify(kycMailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected getting business stakeholder documents from Mirakl",
				String.format("Something went wrong getting documents from Mirakl for shop Id [%s]%n%s",
						String.join(",", kycDocumentBusinessStakeHolderInfoModel.getClientUserId()),
						MiraklLoggingErrorsUtil.stringify(miraklException)));
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldReturnSameInputWhenAuthorizationLetterIsRequiredAndRequiresKYCButDocumentsDoesNotExists() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.requiresKYC(true)
				.requiresLetterOfAuthorization(true)
				.businessStakeholderMiraklNumber(1)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID).build();
		//@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		assertThat(kycDocumentBusinessStakeHolderInfoModel).isEqualTo(result);
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldReturnSameInputWhenRequiresKYCButDocumentsDoesNotExists() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.requiresKYC(true)
				.businessStakeholderMiraklNumber(1)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID).build();
		//@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		assertThat(kycDocumentBusinessStakeHolderInfoModel).isEqualTo(result);
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldReturnSameInputWhenRequiresLetterOfAuthorizationButDocumentsDoesNotExists() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder()
				.requiresLetterOfAuthorization(true)
				.businessStakeholderMiraklNumber(1)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID).build();
		//@formatter:on

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		assertThat(kycDocumentBusinessStakeHolderInfoModel).isEqualTo(result);
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldPopulateKYCInfoModelWithDocumentInformationAndReturnDocumentsReturnedByStrategies_whenLetterOfAuthorizationIsRequiredAndLetterOfAuthorizationDocumentIsFilled() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.businessStakeholderMiraklNumber(1)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on

		final KYCDocumentModel kycDocumentModelProofOfIdentityFront = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentModel kycDocumentModelProofOfIdentityBack = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK).build();
		final KYCDocumentModel kycDocumentModelLetterOfAuthorization = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_AUTHORIZATION).build();

		final MiraklShopDocument miraklShopProofOfIdentityDocumentFront = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentFront.setTypeCode("hw-bsh1-proof-identity-front");
		final MiraklShopDocument miraklShopProofOfIdentityDocumentBack = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentBack.setTypeCode("hw-bsh1-proof-identity-back");
		final MiraklShopDocument miraklShopLetterOfAuthorizationDocument = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentBack.setTypeCode("hw-bsh-letter-authorization");

		final List<MiraklShopDocument> miraklShopDocumentsList = List.of(miraklShopProofOfIdentityDocumentFront,
				miraklShopProofOfIdentityDocumentBack, miraklShopLetterOfAuthorizationDocument);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID))))
						.thenReturn(miraklShopDocumentsList);

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModelWithMiraklDocumentsShopInformation = kycDocumentBusinessStakeHolderInfoModel
				.toBuilder().miraklShopDocuments(miraklShopDocumentsList).build();

		when(proofOfIdentityStrategyExecutorMock
				.execute(kycDocumentBusinessStakeholderInfoModelWithMiraklDocumentsShopInformation))
						.thenReturn(List.of(List.of(kycDocumentModelProofOfIdentityFront,
								kycDocumentModelProofOfIdentityBack, kycDocumentModelLetterOfAuthorization)));

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID)));
		verifyNoMoreInteractions(miraklMarketplacePlatformOperatorApiClientMock);

		assertThat(result.getDocuments()).containsExactlyInAnyOrder(kycDocumentModelProofOfIdentityFront,
				kycDocumentModelProofOfIdentityBack, kycDocumentModelLetterOfAuthorization);
	}

	@Test
	void getBusinessStakeholderDocumentsSelectedBySeller_shouldNotPopulateKYCInfoModelWithDocumentInformationWhenLetterOfAuthorizationIsRequiredAndLetterOfAuthorizationDocumentIsNotFilled() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.businessStakeholderMiraklNumber(1)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.clientUserId(MIRAKL_SHOP_ID)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on

		final MiraklShopDocument miraklShopProofOfIdentityDocumentFront = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentFront.setTypeCode("hw-bsh1-proof-identity-front");
		final MiraklShopDocument miraklShopProofOfIdentityDocumentBack = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentBack.setTypeCode("hw-bsh1-proof-identity-back");

		final List<MiraklShopDocument> miraklShopDocumentsList = List.of(miraklShopProofOfIdentityDocumentFront,
				miraklShopProofOfIdentityDocumentBack);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID))))
						.thenReturn(miraklShopDocumentsList);

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(kycDocumentBusinessStakeHolderInfoModel);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID)));
		verifyNoMoreInteractions(miraklMarketplacePlatformOperatorApiClientMock);

		assertThat(result.getDocuments()).isEmpty();
	}

	@MethodSource("documents")
	@ParameterizedTest
	void getBusinessStakeholderDocumentsSelectedBySeller_whenDocumentsAremissingForShop_shouldReturnShopDocument_andNeverSelectDocuments(
			final KYCDocumentBusinessStakeHolderInfoModel document) {
		doReturn(document).when(testObj)
				.populateMiraklShopBusinessStakeholderDocuments(KYCDocumentBusinessStakeHolderInfoModelMock);

		final KYCDocumentBusinessStakeHolderInfoModel result = testObj
				.getBusinessStakeholderDocumentsSelectedBySeller(KYCDocumentBusinessStakeHolderInfoModelMock);

		assertThat(result).isEqualTo(document);
		verify(proofOfIdentityStrategyExecutorMock, never()).execute(any());
	}

	private static Stream<Arguments> documents() {
		// Case 1: Document needs LOA and KYC but does not exist in Mirakl
		final KYCDocumentBusinessStakeHolderInfoModel case1 = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresLetterOfAuthorization(true).requiresKYC(true).build());
		when(case1.existsDocumentInMirakl()).thenReturn(false);

		// Case 2: Document needs LOA and KYC, exists in Mirakl but does not have LoA
		// document in Mirakl
		final KYCDocumentBusinessStakeHolderInfoModel case2 = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresLetterOfAuthorization(true).requiresKYC(true).build());
		when(case2.existsDocumentInMirakl()).thenReturn(true);
		when(case2.existsDocumentInMirakl()).thenReturn(false);

		// Case 3: Document needs LOA (not KYC), but does not have a LoA document in
		// Mirakl
		final KYCDocumentBusinessStakeHolderInfoModel case3 = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresLetterOfAuthorization(true).requiresKYC(false).build());
		when(case3.existsLetterOfAuthorizationDocumentInMirakl()).thenReturn(false);

		// Case 4: Document needs KYC, but does not have a document in Mirakl
		final KYCDocumentBusinessStakeHolderInfoModel case4 = spy(KYCDocumentBusinessStakeHolderInfoModel.builder()
				.requiresLetterOfAuthorization(false).requiresKYC(true).build());
		when(case4.existsDocumentInMirakl()).thenReturn(false);

		return Stream.of(Arguments.of(case1), Arguments.of(case2), Arguments.of(case3), Arguments.of(case4));
	}

}
