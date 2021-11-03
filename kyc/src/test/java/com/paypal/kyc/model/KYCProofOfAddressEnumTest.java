package com.paypal.kyc.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KYCProofOfAddressEnumTest {

	@Test
	void getMiraklFields_shouldReturnProofOfAddressFrontWhenProofOfAddress() {
		final List<String> result = KYCProofOfAddressEnum.getMiraklFields();

		assertThat(result).containsExactlyInAnyOrder(KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
	}

}
