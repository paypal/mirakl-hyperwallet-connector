package com.paypal.kyc.consumer;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.service.KYCBusinessStakeholderNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMQBusinessStakeHolderKYCConsumerTest {

	@InjectMocks
	private RabbitMQBusinessStakeHolderKYCConsumer testObj;

	@Mock
	private KYCBusinessStakeholderNotificationService kycBusinessStakeholderNotificationServiceMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Test
	void sendKycNotification_shouldCallKycBusinessStakeholderNotificationService() {
		testObj.sendKycNotification(hyperwalletWebhookNotificationMock);

		verify(kycBusinessStakeholderNotificationServiceMock)
				.updateBusinessStakeholderKYCStatus(hyperwalletWebhookNotificationMock);
	}

}
