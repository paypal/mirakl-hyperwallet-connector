package com.paypal.kyc.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.kyc.model.KYCConstants.HwDocuments.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCConstantsTest {

	@Test
	void getAllDocumentsTypes_shouldReturnListOfProofIdentityFrontAndProofIdentityBackAndProofOfAddress() {
		final List<String> result = KYCConstants.HwDocuments.getAllDocumentsTypes();

		assertThat(result).containsExactlyInAnyOrder(PROOF_OF_IDENTITY_FRONT, PROOF_OF_IDENTITY_BACK, PROOF_OF_ADDRESS);
	}

}
