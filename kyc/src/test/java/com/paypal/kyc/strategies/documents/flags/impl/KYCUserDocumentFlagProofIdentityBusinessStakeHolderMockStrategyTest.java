package com.paypal.kyc.strategies.documents.flags.impl;

import com.paypal.infrastructure.BusinessStakeholderTestHelper;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagProofIdentityBusinessStakeHolderMockStrategyTest {

	private static final String CLIENT_ID = "clientId";

	private static final String STAKEHOLDER = "stakeholder";

	private static final String CUSTOM_VALUE = "customValue";

	@Spy
	@InjectMocks
	private KYCUserDocumentFlagProofIdentityBusinessStakeHolderMockStrategy testObj;

	@Mock
	private BusinessStakeholderTestHelper businessStakeholderTestHelperMock;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModelMock;

	@Test
	void execute_shouldGetStakeholdersToValidate_andFillMiraklProofIdentityOrBusinessFlagStatus_andReturnNull() {
		final List<String> customValues = Collections.singletonList(CUSTOM_VALUE);
		final List<String> stakeholdersToVerify = Collections.singletonList(STAKEHOLDER);
		when(kycUserDocumentFlagsNotificationBodyModelMock.getClientUserId()).thenReturn(CLIENT_ID);
		when(businessStakeholderTestHelperMock.getRequiresVerificationBstk(CLIENT_ID)).thenReturn(stakeholdersToVerify);
		when(miraklBusinessStakeholderDocumentsExtractServiceMock
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(CLIENT_ID, stakeholdersToVerify))
						.thenReturn(customValues);
		doNothing().when(testObj).fillMiraklProofIdentityOrBusinessFlagStatus(
				kycUserDocumentFlagsNotificationBodyModelMock, customValues);

		final Void result = testObj.execute(kycUserDocumentFlagsNotificationBodyModelMock);

		assertThat(result).isNull();
		verify(miraklBusinessStakeholderDocumentsExtractServiceMock)
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(CLIENT_ID, stakeholdersToVerify);
		verify(testObj).fillMiraklProofIdentityOrBusinessFlagStatus(kycUserDocumentFlagsNotificationBodyModelMock,
				customValues);
	}

}
