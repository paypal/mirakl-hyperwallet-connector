package com.paypal.kyc.strategies.documents.files.mirakl.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCProofOfIdentityEnum;
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
class MiraklProofOfIdentityBusinessStakeholderStrategyTest {

	@InjectMocks
	private MiraklProofOfIdentityBusinessStakeholderStrategy testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel;

	private final static int BUSINESS_STAKEHOLDER_NUMBER = 1;

	private static final String HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD = "hw-stakeholder-proof-identity-type-1";

	private static MockedStatic<KYCProofOfIdentityEnum> kycProofOfIdentityEnumMockedStatic;

	@AfterAll
	static void afterAll() {
		kycProofOfIdentityEnumMockedStatic.close();
	}

	@BeforeEach
	void setUp() {
		testObj = new MiraklProofOfIdentityBusinessStakeholderStrategy(miraklMarketplacePlatformOperatorApiClientMock);

		//@formatter:off
        kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder()
                .proofOfIdentity(List.of(new MiraklAdditionalFieldValue.MiraklValueListAdditionalFieldValue(
                        HYPERWALLET_BUSINESS_STAKEHOLDER_PROOF_IDENTITY_TYPE_FIELD, "PASSPORT")), BUSINESS_STAKEHOLDER_NUMBER)
                .build();
        //@formatter:on
	}

	@Test
	void isApplicable_shouldReturnTrueWhenIsProofOfIdentity() {
		final boolean result = testObj
				.isApplicable(kycDocumentBusinessStakeHolderInfoModel.toBuilder().requiresKYC(true).build());

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenIsNotProofOfIdentity() {
		kycDocumentBusinessStakeHolderInfoModel = KYCDocumentBusinessStakeHolderInfoModel.builder().build();

		final boolean result = testObj.isApplicable(kycDocumentBusinessStakeHolderInfoModel);

		assertThat(result).isFalse();
	}

	@Test
	void getMiraklFieldNames_shouldGetMiraklFieldNamesFromKYCProofOfIdentityEnum() {
		kycProofOfIdentityEnumMockedStatic = mockStatic(KYCProofOfIdentityEnum.class);

		testObj.getMiraklFieldNames(kycDocumentBusinessStakeHolderInfoModel);

		kycProofOfIdentityEnumMockedStatic.verify(() -> KYCProofOfIdentityEnum.getMiraklFields(
				kycDocumentBusinessStakeHolderInfoModel.getProofOfIdentity(),
				kycDocumentBusinessStakeHolderInfoModel.getBusinessStakeholderMiraklNumber()));
	}

}
