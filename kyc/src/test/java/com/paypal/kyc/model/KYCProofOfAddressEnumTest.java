package com.paypal.kyc.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Fails randomly on first compilation with Gradle 6.x. Works with Gradle 7.x/Java 17. Disabled until upgrade")
class KYCProofOfAddressEnumTest {

	@Test
	void getMiraklFields_shouldReturnProofOfAddressFrontWhenProofOfAddress() {
		final List<String> result = KYCProofOfAddressEnum.getMiraklFields();

		assertThat(result).containsExactlyInAnyOrder(KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
	}

}
