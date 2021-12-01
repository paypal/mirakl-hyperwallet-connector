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
class KYCDocumentInfoModelToProofOfAddressHyperwalletVerificationDocumentSellerStrategyTest {

	private static final String CLIENT_USER_ID = "2000";

	private static final String USR_TOKEN = "usr-32145687";

	@InjectMocks
	private KYCDocumentInfoModelToProofOAddressHyperwalletVerificationDocumentStrategy testObj;

	@Mock
	private File proofOfIdentityFileFrontMock;

	private KYCDocumentModel documentProofOfAddressFront;

	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModel;

	@BeforeEach
	void setUp() {
		documentProofOfAddressFront = KYCDocumentModel.builder()
				.documentFieldName(KYCConstants.HwDocuments.PROOF_OF_ADDRESS).file(proofOfIdentityFileFrontMock)
				.build();

		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().clientUserId(CLIENT_USER_ID)
				.userToken(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
						KYCConstants.HYPERWALLET_USER_TOKEN_FIELD, USR_TOKEN)))
				.professional(false)
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "BANK_STATEMENT")))
				.documents(List.of(documentProofOfAddressFront)).build();
	}

	@Test
	void execute_shouldReturnListOfHyperwalletVerificationProofOfAddressDocuments() {
		when(proofOfIdentityFileFrontMock.getAbsolutePath()).thenReturn("/this/is/my/path/file_front.jpg");

		final HyperwalletVerificationDocument result = testObj.execute(kycDocumentSellerInfoModel);

		final ArrayList<Map.Entry<String, String>> uploadFiles = Lists.newArrayList(result.getUploadFiles().entrySet());

		assertThat(result.getCategory()).isEqualTo(KYCDocumentCategoryEnum.ADDRESS.toString());
		assertThat(result.getType()).isEqualTo(KYCProofOfAddressEnum.BANK_STATEMENT.toString());
		assertThat(uploadFiles.get(0).getKey()).isEqualTo("bank_statement_front");
		assertThat(uploadFiles.get(0).getValue()).isEqualTo("/this/is/my/path/file_front.jpg");
	}

	@Test
	void isApplicable_shouldReturnTrueWhenIdentityDocumentsIsNotEmptyAndProofOfAddressIsFilled() {
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIdentityDocumentsIsEmpty() {
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "BANK_STATEMENT")))
				.build();

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenProofOfAddressIsEmpty() {
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.documents(List.of(documentProofOfAddressFront)).build();

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsProfessional() {
		final KYCDocumentSellerInfoModel professional = kycDocumentSellerInfoModel.toBuilder().professional(true)
				.build();

		final boolean result = testObj.isApplicable(professional);

		assertThat(result).isFalse();
	}

}
