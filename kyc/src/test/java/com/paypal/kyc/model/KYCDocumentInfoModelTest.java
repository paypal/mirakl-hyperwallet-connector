package com.paypal.kyc.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCDocumentInfoModelTest {

	private static final String SHOP_ID_2000 = "2000";

	private static final String SHOP_ID_2001 = "2001";

	private static final String USER_TOKEN = "userToken";

	private static final String EUROPE = "EUROPE";

	@Test
	void equals_shouldReturnTrueWhenBothAreEquals() {
		final KYCDocumentInfoModel kycDocumentInfoModelOne = createKYCDocumentInfoModelObject();
		final KYCDocumentInfoModel kycDocumentInfoModelTwo = createKYCDocumentInfoModelObject();

		final boolean result = kycDocumentInfoModelOne.equals(kycDocumentInfoModelTwo);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenBothAreNotEquals() {
		final KYCDocumentInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentInfoModelObject();
		final KYCDocumentInfoModel kycDocumentBusinessStakeHolderInfoModelTwo = createAnotherKYCDocumentInfoModelObject();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne
				.equals(kycDocumentBusinessStakeHolderInfoModelTwo);

		assertThat(result).isFalse();
	}

	@Test
	void equals_shouldReturnTrueWhenSameObjectIsCompared() {
		final KYCDocumentInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentInfoModelObject();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne
				.equals(kycDocumentBusinessStakeHolderInfoModelOne);

		assertThat(result).isTrue();
	}

	@Test
	void equals_shouldReturnFalseWhenComparedWithAnotherInstanceObject() {
		final KYCDocumentInfoModel kycDocumentBusinessStakeHolderInfoModelOne = createKYCDocumentInfoModelObject();

		final Object o = new Object();

		final boolean result = kycDocumentBusinessStakeHolderInfoModelOne.equals(o);

		assertThat(result).isFalse();
	}

	private KYCDocumentInfoModel createKYCDocumentInfoModelObject() {
		//@formatter:off
		return KYCDocumentInfoModel.builder()
				.userToken(USER_TOKEN)
				.clientUserId(SHOP_ID_2001)
				.requiresKYC(Boolean.TRUE)
				.hyperwalletProgram(EUROPE)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on
	}

	private KYCDocumentInfoModel createAnotherKYCDocumentInfoModelObject() {
		//@formatter:off
		return KYCDocumentInfoModel.builder()
				.userToken(USER_TOKEN)
				.clientUserId(SHOP_ID_2000)
				.requiresKYC(Boolean.TRUE)
				.hyperwalletProgram(EUROPE)
				.proofOfIdentity(KYCProofOfIdentityEnum.GOVERNMENT_ID)
				.build();
		//@formatter:on
	}

}
