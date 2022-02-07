package com.paypal.kyc.listeners;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KYCBusinessStakeHolderListenerTest {

	private static final String NOTIFICATION_TYPE = "Business stakeholder KYC";

	private KYCBusinessStakeHolderListener testObj;

	@Mock
	private KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCBusinessStakeHolderListener(null, null, kycBusinessStakeholderNotificationServiceMock, null);
	}

	@Test
	void processNotification_shouldCallUpdateBusinessStakeholderKYCStatus() {
		testObj.processNotification(hyperwalletWebhookNotificationMock);

		verify(kycBusinessStakeholderNotificationServiceMock)
				.updateBusinessStakeholderKYCStatus(hyperwalletWebhookNotificationMock);
	}

	@Test
	void getNotificationType_shouldReturnBusinessStakeholderKyc() {
		final String result = testObj.getNotificationType();

		assertThat(result).isEqualTo(NOTIFICATION_TYPE);
	}

}
