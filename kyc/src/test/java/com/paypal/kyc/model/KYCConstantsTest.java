package com.paypal.kyc.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.paypal.kyc.model.KYCConstants.HwDocuments.*;

@ExtendWith(MockitoExtension.class)
class KYCConstantsTest {

	@Test
	void getAllDocumentsTypes_shouldReturnListOfProofIdentityFrontAndProofIdentityBackAndProofOfAddress() {

		final List<String> result = KYCConstants.HwDocuments.getAllDocumentsTypes();

		Assertions.assertThat(result).containsExactlyInAnyOrder(PROOF_OF_IDENTITY_FRONT, PROOF_OF_IDENTITY_BACK,
				PROOF_OF_ADDRESS);

	}

}
