package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.strategies.status.impl.KYCBusinessStakeholderStatusExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCBusinessStakeholderNotificationServiceImplTest {

	@InjectMocks
	private KYCBusinessStakeholderNotificationServiceImpl testObj;

	@Mock
	private Converter<Object, KYCBusinessStakeholderStatusNotificationBodyModel> hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock;

	@Mock
	private KYCBusinessStakeholderStatusExecutor kycBusinessStakeholderStatusExecutorMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderNotificationMock;

	@Test
	void updateBusinessStakeholderKYCStatus_shouldConvertAndTreatThenIncomingNotification() {
		when(hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock
				.convert(hyperwalletWebhookNotificationMock)).thenReturn(kycBusinessStakeholderNotificationMock);

		testObj.updateBusinessStakeholderKYCStatus(hyperwalletWebhookNotificationMock);

		verify(hyperWalletObjectToKYCBusinessStakeholderStatusNotificationBodyModelConverterMock)
				.convert(hyperwalletWebhookNotificationMock);
		verify(kycBusinessStakeholderStatusExecutorMock).execute(kycBusinessStakeholderNotificationMock);
	}

}
