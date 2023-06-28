package com.paypal.kyc.sellersdocumentextraction.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.documentextractioncommons.model.KYCProofOfIdentityEnum;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentCategoryEnum;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCDocumentInfoModelToProofOfIdentityHyperwalletVerificationDocumentSellerStrategyTest {

	private static final String CLIENT_USER_ID = "2000";

	private static final String USR_TOKEN = "usr-32145687";

	private static final String COUNTRY_ISOCODE = "US";

	@InjectMocks
	private KYCDocumentInfoModelToProofOfIdentityHyperwalletVerificationDocumentStrategy testObj;

	@Mock
	private File proofOfIdentityFileFrontMock;

	@Mock
	private File proofOfIdentityFileBackMock;

	private KYCDocumentModel documentProofOfIdentityFront;

	private KYCDocumentModel documentProofOfIdentityBack;

	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModel;

	@BeforeEach
	void setUp() {
		//@formatter:off
		documentProofOfIdentityFront = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_FRONT)
				.file(proofOfIdentityFileFrontMock)
				.build();
		//@formatter:on

		//@formatter:off
		documentProofOfIdentityBack = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_IDENTITY_BACK)
				.file(proofOfIdentityFileBackMock)
				.build();
		//@formatter:on
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN)))
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "GOVERNMENT_ID")))
				.countryIsoCode(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_COUNTRY_ISOCODE, "US")))
				.documents(List.of(documentProofOfIdentityFront, documentProofOfIdentityBack)).build();
		//@formatter:off
	}

	@Test
	void execute_shouldReturnListOfHyperwalletVerificationProofOfIdentityDocuments() {
		when(proofOfIdentityFileFrontMock.getAbsolutePath()).thenReturn("/this/is/my/path/file_front.jpg");
		when(proofOfIdentityFileBackMock.getAbsolutePath()).thenReturn("/this/is/my/path/file_back.jpg");

		final HyperwalletVerificationDocument result = testObj.execute(kycDocumentSellerInfoModel);

		assertThat(result.getCategory()).isEqualTo(KYCDocumentCategoryEnum.IDENTIFICATION.toString());
		assertThat(result.getCountry()).isEqualTo(COUNTRY_ISOCODE);
		assertThat(result.getType()).isEqualTo(KYCProofOfIdentityEnum.GOVERNMENT_ID.toString());

		final ArrayList<Map.Entry<String, String>> uploadFiles = Lists.newArrayList(result.getUploadFiles().entrySet());

		assertThat(uploadFiles.get(1).getKey()).isEqualTo("government_id_back");
		assertThat(uploadFiles.get(1).getValue()).isEqualTo("/this/is/my/path/file_back.jpg");
		assertThat(uploadFiles.get(0).getKey()).isEqualTo("government_id_front");
		assertThat(uploadFiles.get(0).getValue()).isEqualTo("/this/is/my/path/file_front.jpg");
	}

	@Test
	void isApplicable_shouldReturnTrueWhenIdentityDocumentsIsNotEmptyAndProofOfIdentityIsFilled() {
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIdentityDocumentsIsEmpty() {
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
				KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "GOVERNMENT_ID"))).build();

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenProofOfIdentityIsEmpty() {
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().documents(List.of(documentProofOfIdentityFront, documentProofOfIdentityBack)).build();

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsProfessional() {
		final KYCDocumentSellerInfoModel professional = kycDocumentSellerInfoModel.toBuilder().professional(true).build();

		final boolean result = testObj.isApplicable(professional);

		assertThat(result).isFalse();
	}
}
