package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.kyc.converter.HyperWalletObjectToKYCUserDocumentFlagsNotificationBodyModelConverter;
import com.paypal.kyc.converter.HyperWalletObjectToKYCUserStatusNotificationBodyModelConverter;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.strategies.documents.flags.impl.KYCUserDocumentFlagsFactory;
import com.paypal.kyc.strategies.status.impl.KYCUserStatusFactory;
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
	private KYCUserStatusFactory kycUserStatusFactoryMock;

	@Mock
	private KYCUserDocumentFlagsFactory kycUserDocumentFlagsFactoryMock;

	@Mock
	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModelMock;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModelMock;

	@BeforeEach
	void setUp() {
		testObj = new KYCUserNotificationServiceImpl(kycUserStatusFactoryMock, kycUserDocumentFlagsFactoryMock,
				kycUserStatusNotificationBodyModelConverterMock,
				kycUserDocumentFlagsNotificationBodyModelConverterMock);
		when(hyperwalletWebhookNotificationMock.getObject()).thenReturn(nonTransformedNotificationBodyObjectMock);
	}

	@Test
	void processKycUserNotification_shouldExecuteStrategy() {
		when(kycUserStatusNotificationBodyModelConverterMock.convert(nonTransformedNotificationBodyObjectMock))
				.thenReturn(kycUserStatusNotificationBodyModelMock);

		testObj.updateUserKYCStatus(hyperwalletWebhookNotificationMock);

		verify(kycUserStatusFactoryMock).execute(kycUserStatusNotificationBodyModelMock);
	}

	@Test
	void updateUserDocumentsFlags_shouldExecuteDocumentStrategy() {
		when(kycUserDocumentFlagsNotificationBodyModelConverterMock.convert(nonTransformedNotificationBodyObjectMock))
				.thenReturn(kycUserDocumentFlagsNotificationBodyModelMock);

		testObj.updateUserDocumentsFlags(hyperwalletWebhookNotificationMock);

		verify(kycUserDocumentFlagsFactoryMock).execute(kycUserDocumentFlagsNotificationBodyModelMock);

	}

}
