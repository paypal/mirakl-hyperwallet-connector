package com.paypal.kyc.incomingnotifications.services;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.incomingnotifications.services.converters.HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter;
import com.paypal.kyc.incomingnotifications.services.converters.HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.services.KYCUserNotificationServiceImpl;
import com.paypal.kyc.incomingnotifications.services.KYCUserDocumentFlagsExecutor;
import com.paypal.kyc.incomingnotifications.services.KYCUserStatusExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KYCUserNotificationServiceImplTest {

	private KYCUserNotificationServiceImpl testObj;

	@Mock
	private HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter kycUserStatusNotificationBodyModelConverterMock;

	@Mock
	private HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter kycUserDocumentFlagsNotificationBodyModelConverterMock;

	@Mock
	private HyperwalletWebhookNotification hyperwalletWebhookNotificationMock;

	@Mock
	private Object nonTransformedNotificationBodyObjectMock;

	@Mock
	private KYCUserStatusExecutor kycUserStatusExecutorMock;

	@Mock
	private KYCUserDocumentFlagsExecutor kycUserDocumentFlagsExecutorMock;

	@Mock
	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModelMock;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModelMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserNotificationServiceImpl(kycUserStatusExecutorMock, kycUserDocumentFlagsExecutorMock,
				kycUserStatusNotificationBodyModelConverterMock,
				kycUserDocumentFlagsNotificationBodyModelConverterMock);
		when(hyperwalletWebhookNotificationMock.getObject()).thenReturn(nonTransformedNotificationBodyObjectMock);
	}

	@Test
	void processKycUserNotification_shouldExecuteStrategy() {
		when(kycUserStatusNotificationBodyModelConverterMock.convert(nonTransformedNotificationBodyObjectMock))
				.thenReturn(kycUserStatusNotificationBodyModelMock);

		testObj.updateUserKYCStatus(hyperwalletWebhookNotificationMock);

		verify(kycUserStatusExecutorMock).execute(kycUserStatusNotificationBodyModelMock);
	}

	@Test
	void updateUserDocumentsFlags_shouldExecuteDocumentStrategy() {
		when(kycUserDocumentFlagsNotificationBodyModelConverterMock.convert(nonTransformedNotificationBodyObjectMock))
				.thenReturn(kycUserDocumentFlagsNotificationBodyModelMock);

		testObj.updateUserDocumentsFlags(hyperwalletWebhookNotificationMock);

		verify(kycUserDocumentFlagsExecutorMock).execute(kycUserDocumentFlagsNotificationBodyModelMock);
	}

}
