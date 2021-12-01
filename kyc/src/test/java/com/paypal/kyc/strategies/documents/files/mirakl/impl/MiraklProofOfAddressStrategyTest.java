package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.model.KYCProofOfAddressEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MiraklProofOfAddressStrategyTest {

	private MiraklProofOfAddressStrategy testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	private KYCDocumentSellerInfoModel kycDocumentSellerInfoModel;

	@BeforeEach
	void setUp() {
		testObj = new MiraklProofOfAddressStrategy(miraklMarketplacePlatformOperatorApiClientMock);

		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfAddress(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_ADDRESS_FIELD, "TAX_RETURN")))
				.build();
		//@formatter:on
	}

	@Test
	void isApplicable_shouldReturnTrueWhenIsProofOfAddressAndObjectReceivedAsParameterIsKYCDocumentSellerInfoModel() {
		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsProofOfBusinessAndObjectReceivedAsParameterIsNotKYCDocumentSellerInfoModel() {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().build();

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsNotProofOfAddressAndObjectReceivedAsParameterIsKYCDocumentSellerInfoModel() {
		//@formatter:off
		kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_IND_PROOF_OF_IDENTITY_FIELD, "PASSPORT")))
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(kycDocumentSellerInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void getMiraklFieldNames_shouldGetMiraklFieldNamesFromKYCProofOfAddressEnum() {
		final MockedStatic<KYCProofOfAddressEnum> kycProofOfAddressEnumMockedStatic = mockStatic(
				KYCProofOfAddressEnum.class);

		testObj.getMiraklFieldNames(kycDocumentSellerInfoModel);

		kycProofOfAddressEnumMockedStatic.verify(KYCProofOfAddressEnum::getMiraklFields);
	}

}
