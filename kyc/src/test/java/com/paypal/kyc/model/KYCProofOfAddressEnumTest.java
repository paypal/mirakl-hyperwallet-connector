package com.paypal.kyc.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class KYCProofOfAddressEnumTest {

	@Test
	void getMiraklFields_shouldReturnProofOfAddressFrontWhenProofOfAddress() {

		final List<String> result = KYCProofOfAddressEnum.getMiraklFields();

		Assertions.assertThat(result).containsExactlyInAnyOrder(KYCConstants.HwDocuments.PROOF_OF_ADDRESS);
	}

}
