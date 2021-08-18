package com.paypal.kyc.converter;

import com.paypal.kyc.model.KYCDocumentNotificationModel;
import com.paypal.kyc.model.KYCDocumentTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCDocumentNotificationModelToMiraklFieldTypeCodesConverterTest {

	private static final String INDIVIDUAL_PROOF_IDENTITY_BACK = "hw-ind-proof-identity-back";

	private static final String INDIVIDUAL_PROOF_IDENTITY_FRONT = "hw-ind-proof-identity-front";

	private static final String INDIVIDUAL_PROOF_ADDRESS = "hw-ind-proof-address";

	private static final String BUSINESS_PROOF_FRONT = "hw-prof-proof-business-front";

	@InjectMocks
	private KYCDocumentNotificationModelToMiraklFieldTypeCodesConverter testObj;

	@Mock
	private KYCDocumentNotificationModel kycDocumentNotificationModelMock;

	@Test
	void convert_shouldReturnCorrectDocuments_whenDocumentTypeIsGovernmentId() {
		when(kycDocumentNotificationModelMock.getDocumentType()).thenReturn(KYCDocumentTypeEnum.GOVERNMENT_ID);

		final List<String> result = testObj.convert(kycDocumentNotificationModelMock);

		assertThat(result).isNotEmpty().contains(INDIVIDUAL_PROOF_IDENTITY_BACK, INDIVIDUAL_PROOF_IDENTITY_FRONT);
		;
	}

	@Test
	void convert_shouldReturnCorrectDocuments_whenDocumentTypeIsDriversLicense() {
		when(kycDocumentNotificationModelMock.getDocumentType()).thenReturn(KYCDocumentTypeEnum.DRIVERS_LICENSE);

		final List<String> result = testObj.convert(kycDocumentNotificationModelMock);

		assertThat(result).isNotEmpty().contains(INDIVIDUAL_PROOF_IDENTITY_BACK, INDIVIDUAL_PROOF_IDENTITY_FRONT);
	}

	@Test
	void convert_shouldReturnCorrectDocuments_whenDocumentTypeIsPassport() {
		when(kycDocumentNotificationModelMock.getDocumentType()).thenReturn(KYCDocumentTypeEnum.PASSPORT);

		final List<String> result = testObj.convert(kycDocumentNotificationModelMock);

		assertThat(result).isNotEmpty().contains(INDIVIDUAL_PROOF_ADDRESS);
		;
	}

	@Test
	void convert_shouldReturnCorrectDocuments_whenDocumentTypeIsIncorporation() {
		when(kycDocumentNotificationModelMock.getDocumentType()).thenReturn(KYCDocumentTypeEnum.INCORPORATION);

		final List<String> result = testObj.convert(kycDocumentNotificationModelMock);

		assertThat(result).isNotEmpty().contains(BUSINESS_PROOF_FRONT);
	}

}
