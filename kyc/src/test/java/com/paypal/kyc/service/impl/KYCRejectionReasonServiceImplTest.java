package com.paypal.kyc.service.impl;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.kyc.model.KYCRejectionReasonTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KYCRejectionReasonServiceImplTest {

	@InjectMocks
	private KYCRejectionReasonServiceImpl testObj;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForLevel(LogTracker.LogLevel.WARN)
			.recordForType(KYCRejectionReasonServiceImpl.class);

	@Test
	void getReasonTypes_shouldReturnVERIFICATIONSTATUS_IND_REQUIRED_WhenVerificationStatusIsREQUIREDAndUserIsINDIVIDUAL() {
		final Map<String, String> object = Map.of("verificationStatus", "REQUIRED", "profileType", "INDIVIDUAL");

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(result).containsExactlyInAnyOrder(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_IND_REQUIRED);
	}

	@Test
	void getReasonTypes_shouldReturnVERIFICATIONSTATUS_PROF_REQUIRED_WhenVerificationStatusIsREQUIREDAndUserIsBUSINESS() {
		final Map<String, String> object = Map.of("verificationStatus", "REQUIRED", "profileType", "BUSINESS");

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(result).containsExactlyInAnyOrder(KYCRejectionReasonTypeEnum.VERIFICATIONSTATUS_PROF_REQUIRED);
	}

	@Test
	void getReasonTypes_shouldReturnBUSINESS_STAKEHOLDER_REQUIRED_WhenBusinessStakeholderVerificationStatusIsREQUIRED() {
		final Map<String, String> object = Map.of("businessStakeholderVerificationStatus", "REQUIRED");

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(result).containsExactlyInAnyOrder(KYCRejectionReasonTypeEnum.BUSINESS_STAKEHOLDER_REQUIRED);
	}

	@Test
	void getReasonTypes_shouldReturnLETTER_OF_AUTHORIZATION_REQUIRED_WhenLetterOfAuthorizationStatusIsREQUIRED() {
		final Map<String, String> object = Map.of("letterOfAuthorizationStatus", "REQUIRED");

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(result).containsExactlyInAnyOrder(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED);
	}

	@Test
	void getReasonTypes_shouldReturnLETTER_OF_AUTHORIZATION_REQUIREDAndBUSINESS_STAKEHOLDER_REQUIRED_WhenLetterOfAuthorizationStatusAndBusinessStakeholderVerificationStatusAreREQUIRED() {
		final Map<String, String> object = Map.of("letterOfAuthorizationStatus", "REQUIRED",
				"businessStakeholderVerificationStatus", "REQUIRED");

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(result).containsExactlyInAnyOrder(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED,
				KYCRejectionReasonTypeEnum.BUSINESS_STAKEHOLDER_REQUIRED);
	}

	@Test
	void getReasonTypes_shouldReturnEmptyList_WhenNoStatusAreDefined() {

		Map<String, String> object = Map.of();

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(result).isEmpty();
	}

	@Test
	void getReasonTypes_shouldReturnEmptyList_WhenObjectReceivedIsNotAMapInstance() {

		Set<String> object = Set.of();

		final List<KYCRejectionReasonTypeEnum> result = testObj.getReasonTypes(object);

		assertThat(logTrackerStub.contains("The notification object received is not a Map instance.")).isTrue();

		assertThat(result).isEmpty();
	}

}
