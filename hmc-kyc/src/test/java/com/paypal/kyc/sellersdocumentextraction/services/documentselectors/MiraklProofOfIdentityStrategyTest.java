package com.paypal.kyc.sellersdocumentextraction.services.documentselectors;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCProofOfIdentityEnum;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MiraklProofOfIdentityStrategyTest {

	private static MockedStatic<KYCProofOfIdentityEnum> kycProofOfIdentityEnumMockedStatic;

	@InjectMocks
	private MiraklProofOfIdentitySelectorStrategy testObj;

	@Mock
	private MiraklClient miraklClientMock;

	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModel;

	@AfterAll
	static void afterAll() {
		kycProofOfIdentityEnumMockedStatic.close();
	}

	@BeforeEach
	void setUp() {
		testObj = new MiraklProofOfIdentitySelectorStrategy(miraklClientMock);

		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "PASSPORT")))
				.build();
		//@formatter:on
	}

	@Test
	void isApplicable_shouldReturnTrueWhenIsProofOfIdentityAndObjectReceivedAsParameterIsKYCDocumentSellerInfoModel() {
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsNotProofOfIdentityAndObjectReceivedAsParameterIsKYCDocumentSellerInfoModel() {
		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "TAX_RETURN")))
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsProofOfIdentityAndIsProfessional() {
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder().professional(true).build();

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsProofOfBusinessAndObjectReceivedAsParameterIsNotKYCDocumentSellerInfoModel() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().build();

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void getMiraklFieldNames_shouldGetMiraklFieldNamesFromKYCProofOfIdentityEnum() {
		kycProofOfIdentityEnumMockedStatic = mockStatic(KYCProofOfIdentityEnum.class);

		testObj.getMiraklFieldNames(kycDocumentSellerInfoModel);

		kycProofOfIdentityEnumMockedStatic
				.verify(() -> KYCProofOfIdentityEnum.getMiraklFields(kycDocumentSellerInfoModel.getProofOfIdentity()));
	}

}
