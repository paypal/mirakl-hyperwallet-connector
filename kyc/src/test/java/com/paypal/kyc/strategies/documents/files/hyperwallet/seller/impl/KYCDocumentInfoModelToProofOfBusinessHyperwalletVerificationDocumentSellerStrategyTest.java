package com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.kyc.model.*;
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
class KYCDocumentInfoModelToProofOfBusinessHyperwalletVerificationDocumentSellerStrategyTest {

	@InjectMocks
	private KYCDocumentInfoModelToProofOfBusinessHyperwalletVerificationDocumentStrategy testObj;

	private KYCDocumentModel documentProofOfBusinessFront;

	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModel;

	@Mock
	private File proofOfBusinessFileFrontMock;

	private static final String CLIENT_USER_ID = "2000";

	private static final String USR_TOKEN = "usrToken";

	@BeforeEach
	void setUp() {
		//@formatter:off
		documentProofOfBusinessFront = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_BUSINESS).file(proofOfBusinessFileFrontMock)
				.build();

		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN)))
				.professional(true)
				.proofOfBusiness(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_PROF_PROOF_OF_BUSINESS_FIELD, "INCORPORATION")))
				.documents(List.of(documentProofOfBusinessFront))
				.build();
		//@formatter:on
	}

	@Test
	void execute_shouldReturnListOfHyperwalletVerificationProofOfAddressDocuments() {
		when(proofOfBusinessFileFrontMock.getAbsolutePath()).thenReturn("/this/is/my/path/file_front.jpg");

		final HyperwalletVerificationDocument result = testObj.execute(kycDocumentSellerInfoModel);

		final ArrayList<Map.Entry<String, String>> uploadFiles = Lists.newArrayList(result.getUploadFiles().entrySet());

		assertThat(result.getCategory()).isEqualTo(KYCDocumentCategoryEnum.BUSINESS.toString());
		assertThat(result.getType()).isEqualTo(KYCProofOfBusinessEnum.INCORPORATION.toString());
		assertThat(uploadFiles.get(0).getKey()).isEqualTo("incorporation_front");
		assertThat(uploadFiles.get(0).getValue()).isEqualTo("/this/is/my/path/file_front.jpg");
	}

	@Test
	void isApplicable_shouldReturnTrueWhenShopIsProfessionalAndProofOfBusinessIsNotNullAndDocumentsAreNotNull() {
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenShopIsNotProfessional() {
		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN)))
				.professional(false)
				.build();
		//@formatter:on
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenShopIsProfessionalAndProofOfBusinessIsNull() {
		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.clientUserId(CLIENT_USER_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN)))
				.professional(true).documents(List.of(documentProofOfBusinessFront))
				.build();
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);
		//@formatter:on
		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenShopIsProfessionalAndProofOfBusinessIsNotNullAndDocumentsAreNull() {
		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN)))
				.professional(true)
				.proofOfBusiness(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HwDocuments.PROOF_OF_BUSINESS, "INCORPORATION")))
				.build();
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);
		//@formatter:off
		assertThat(result).isFalse();
	}

}
