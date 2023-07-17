package com.paypal.kyc.sellersdocumentextraction.services;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.request.shop.document.MiraklGetShopDocumentsRequest;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import com.paypal.kyc.documentextractioncommons.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCProofOfAddressEnum;
import com.paypal.kyc.documentextractioncommons.services.MiraklDocumentsSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklSellerDocumentDownloadExtractServiceImplTest {

	private static final String MIRAKL_SHOP_ID = "2000";

	@InjectMocks
	private MiraklSellerDocumentDownloadExtractServiceImpl testObj;

	@Mock
	private MiraklClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private MiraklDocumentsSelector proofOfIdentityStrategyExecutorMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Test
	void getDocumentsSelectedBySeller_shouldReturnAnEmptyListWhenNoProofOfAddressNeitherProofOfIdentityHasBeenSelectedBySeller() {
		// formatter:off
		final KYCDocumentSellerInfoModel kycNonSelectedDocuments = KYCDocumentSellerInfoModel.builder()
				.clientUserId(MIRAKL_SHOP_ID).proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT).build();
		// formatter:on

		final KYCDocumentSellerInfoModel result = testObj.getDocumentsSelectedBySeller(kycNonSelectedDocuments);

		assertThat(result.getDocuments()).isEmpty();
	}

	@Test
	void getDocumentsSelectedBySeller_shouldPopulateKYCInfoModelWithDocumentInformationAndReturnDocumentsReturnedByStrategies() {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.clientUserId(MIRAKL_SHOP_ID).proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT).build();

		final KYCDocumentModel kycDocumentModelFront = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT).build();
		final KYCDocumentModel kycDocumentModelBack = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK).build();

		final MiraklShopDocument miraklShopProofOfIdentityDocumentFront = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentFront.setTypeCode("hw-ind-proof-identity-front");
		final MiraklShopDocument miraklShopProofOfIdentityDocumentBack = new MiraklShopDocument();
		miraklShopProofOfIdentityDocumentBack.setTypeCode("hw-ind-proof-identity-back");
		final MiraklShopDocument miraklShopProofOfAddressFront = new MiraklShopDocument();
		miraklShopProofOfAddressFront.setTypeCode("hw-ind-proof-address");

		final List<MiraklShopDocument> miraklShopDocumentsList = List.of(miraklShopProofOfIdentityDocumentFront,
				miraklShopProofOfIdentityDocumentBack, miraklShopProofOfAddressFront);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID))))
						.thenReturn(miraklShopDocumentsList);

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModelWithMiraklDocumentsShopInformation = kycDocumentSellerInfoModel
				.toBuilder().miraklShopDocuments(miraklShopDocumentsList).build();

		when(proofOfIdentityStrategyExecutorMock.execute(kycDocumentSellerInfoModelWithMiraklDocumentsShopInformation))
				.thenReturn(List.of(List.of(kycDocumentModelFront, kycDocumentModelBack)));

		final KYCDocumentSellerInfoModel result = testObj.getDocumentsSelectedBySeller(kycDocumentSellerInfoModel);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID)));
		verifyNoMoreInteractions(miraklMarketplacePlatformOperatorApiClientMock);

		assertThat(result.getDocuments()).containsExactlyInAnyOrder(kycDocumentModelFront, kycDocumentModelBack);
	}

	@Test
	void getDocumentsSelectedBySeller_shouldSendMailNotificationWhenMiraklExceptionIsThrown() {
		// formatter:off
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.clientUserId(MIRAKL_SHOP_ID).proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.proofOfAddress(KYCProofOfAddressEnum.BANK_STATEMENT).build();
		// formatter:on

		final MiraklException miraklException = new MiraklException("Something wrong happened");
		doThrow(miraklException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.getShopDocuments(new MiraklGetShopDocumentsRequest(List.of(MIRAKL_SHOP_ID)));

		testObj.getDocumentsSelectedBySeller(kycDocumentSellerInfoModel);

		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue detected getting documents from Mirakl",
				"Something went wrong getting documents from Mirakl for shop Id [%s]%n%s".formatted(
						String.join(",", kycDocumentSellerInfoModel.getClientUserId()),
						MiraklLoggingErrorsUtil.stringify(miraklException)));
	}

}
