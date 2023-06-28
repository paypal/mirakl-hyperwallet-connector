package com.paypal.kyc.documentextractioncommons.model;

import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.documentextractioncommons.model.KYCProofOfIdentityEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KYCProofOfIdentityEnumTest {

	private final static String PREFIX_FIELD_NAME = KYCConstants.HYPERWALLET_PREFIX + KYCConstants.INDIVIDUAL_PREFIX
			+ KYCConstants.PROOF_IDENTITY_PREFIX;

	@Test
	void getMiraklFields_shouldReturnProofOfIdentityFrontAndBackWhenProofOfIdentityIsNationalCard() {
		final List<String> result = KYCProofOfIdentityEnum.getMiraklFields(KYCProofOfIdentityEnum.GOVERNMENT_ID);

		assertThat(result).containsExactlyInAnyOrder(PREFIX_FIELD_NAME + KYCConstants.PROOF_IDENTITY_SIDE_FRONT,
				PREFIX_FIELD_NAME + KYCConstants.PROOF_IDENTITY_SIDE_BACK);
	}

	@Test
	void getMiraklFields_shouldReturnProofOfIdentityFrontAndBackWhenProofOfIdentityIsDriversLicense() {
		final List<String> result = KYCProofOfIdentityEnum.getMiraklFields(KYCProofOfIdentityEnum.DRIVERS_LICENSE);

		assertThat(result).containsExactlyInAnyOrder(PREFIX_FIELD_NAME + KYCConstants.PROOF_IDENTITY_SIDE_FRONT,
				PREFIX_FIELD_NAME + KYCConstants.PROOF_IDENTITY_SIDE_BACK);
	}

	@Test
	void getMiraklFields_shouldReturnProofOfIdentityFrontWhenProofOfIdentityIsPassport() {
		final List<String> result = KYCProofOfIdentityEnum.getMiraklFields(KYCProofOfIdentityEnum.PASSPORT);

		assertThat(result).containsExactlyInAnyOrder(PREFIX_FIELD_NAME + KYCConstants.PROOF_IDENTITY_SIDE_FRONT);
	}

	@Test
	void getMiraklFields_shouldReturnEmptyListWhenProofOfIdentityEnumIsNull() {
		final List<String> result = KYCProofOfIdentityEnum.getMiraklFields(null);

		assertThat(result).isEmpty();
	}

}
