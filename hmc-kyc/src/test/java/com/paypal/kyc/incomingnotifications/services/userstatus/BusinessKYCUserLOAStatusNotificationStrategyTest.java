package com.paypal.kyc.incomingnotifications.services.userstatus;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.hyperwallet.configuration.HyperwalletProgramsConfiguration;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.incomingnotifications.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessKYCUserLOAStatusNotificationStrategyTest {

	private static final String UK_TOKEN = "ukToken";

	private static final String UK_PROGRAM = "ukProgram";

	private static final String USER_TOKEN = "userToken";

	private static final String MIRAKL_SHOP_ID = "12345";

	private static final String DEFAULT_TOKEN = "defaultToken";

	private static final String DEFAULT_PROGRAM = "defaultProgram";

	private static final String EXCEPTION_MESSAGE = "No Hyperwallet users were found for user token %s in the system instance(s)";

	@Spy
	@InjectMocks
	private BusinessKYCUserLOAStatusNotificationStrategy testObj;

	@Mock
	private UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	private HyperwalletProgramsConfiguration hyperwalletProgramsConfigurationMock;

	@Mock
	private HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration hyperwalletProgramConfiguration1Mock;

	@Mock
	private HyperwalletProgramsConfiguration.HyperwalletProgramConfiguration hyperwalletProgramConfiguration2Mock;

	@Mock
	private MiraklClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModelMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Mock
	private MiraklUpdatedShops miraklUpdatedShopsMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestArgumentCaptor;

	@Test
	void execute_shouldCallUpdateMiraklLOAStatus() {
		mockProgramsConfiguration();
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(anyString()))
				.thenReturn(hyperwalletMock);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(hyperwalletMock.getUser(USER_TOKEN)).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getClientUserId()).thenReturn(MIRAKL_SHOP_ID);
		when(hyperwalletUserMock.getLetterOfAuthorizationStatus())
				.thenReturn(HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED);
		when(miraklMarketplacePlatformOperatorApiClientMock
				.updateShops(miraklUpdateShopsRequestArgumentCaptor.capture())).thenReturn(miraklUpdatedShopsMock);

		testObj.execute(kycBusinessStakeholderStatusNotificationBodyModelMock);

		final MiraklUpdateShopsRequest miraklUpdateShopsRequestArgumentCaptorValue = miraklUpdateShopsRequestArgumentCaptor
				.getValue();
		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue result = (MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) miraklUpdateShopsRequestArgumentCaptorValue
				.getShops().get(0).getAdditionalFieldValues().get(0);

		assertThat(result.getCode())
				.isEqualTo(KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD);
		assertThat(result.getValue()).isEqualTo(Boolean.TRUE.toString());
	}

	@Test
	void execute_whenNoHyperwalletUsersMatchTheGivenToken_shouldReturnNull() {
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(hyperwalletProgramsConfigurationMock.getAllProgramConfigurations()).thenReturn(List.of());

		final Throwable throwable = catchThrowable(
				() -> testObj.execute(kycBusinessStakeholderStatusNotificationBodyModelMock));

		verify(testObj, never()).updateMiraklLOAStatus(any(), any());
		assertThat(throwable).isInstanceOf(HMCException.class);
		assertThat(throwable.getMessage()).isEqualTo(EXCEPTION_MESSAGE.formatted(USER_TOKEN));
	}

	@ParameterizedTest
	@MethodSource("LOAStatuses")
	void updateMiraklLOAStatus_shouldChangeMiraklLOAFlagToTrue_whenLOAIsFailedOrRequired(
			final HyperwalletUser.LetterOfAuthorizationStatus letterOfAuthorizationStatus,
			final Boolean isLOARequired) {
		when(miraklMarketplacePlatformOperatorApiClientMock
				.updateShops(miraklUpdateShopsRequestArgumentCaptor.capture())).thenReturn(miraklUpdatedShopsMock);

		testObj.updateMiraklLOAStatus(MIRAKL_SHOP_ID, letterOfAuthorizationStatus);

		final MiraklUpdateShopsRequest miraklUpdateShopsRequestArgumentCaptorValue = miraklUpdateShopsRequestArgumentCaptor
				.getValue();
		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue result = (MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) miraklUpdateShopsRequestArgumentCaptorValue
				.getShops().get(0).getAdditionalFieldValues().get(0);

		assertThat(result.getCode())
				.isEqualTo(KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD);
		assertThat(result.getValue()).isEqualTo(isLOARequired.toString());
	}

	@ParameterizedTest
	@MethodSource("notificationIsDirectorIsBusinessStakeholderCartesianProduct")
	void isApplicable_shouldReturnTrue_whenBusinessStakeholderIsDirectorAndBusinessContact(final Boolean isDirector,
			final Boolean isBusinessContact, final Boolean resultExpected) {
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getIsBusinessContact())
				.thenReturn(isBusinessContact);
		lenient().when(kycBusinessStakeholderStatusNotificationBodyModelMock.getIsDirector()).thenReturn(isDirector);
		lenient().when(kycBusinessStakeholderStatusNotificationBodyModelMock.getHyperwalletWebhookNotificationType())
				.thenReturn(KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_CREATED);

		final boolean result = testObj.isApplicable(kycBusinessStakeholderStatusNotificationBodyModelMock);

		assertThat(result).isEqualTo(resultExpected);
	}

	public static Stream<Arguments> notificationIsDirectorIsBusinessStakeholderCartesianProduct() {
		return Stream.of(Arguments.of(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE),
				Arguments.of(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE),
				Arguments.of(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE),
				Arguments.of(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE));
	}

	public static Stream<Arguments> LOAStatuses() {
		return Stream.of(Arguments.of(HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED, Boolean.TRUE),
				Arguments.of(HyperwalletUser.LetterOfAuthorizationStatus.NOT_REQUIRED, Boolean.FALSE),
				Arguments.of(HyperwalletUser.LetterOfAuthorizationStatus.UNDER_REVIEW, Boolean.FALSE),
				Arguments.of(HyperwalletUser.LetterOfAuthorizationStatus.VERIFIED, Boolean.FALSE),
				Arguments.of(HyperwalletUser.LetterOfAuthorizationStatus.READY_FOR_REVIEW, Boolean.FALSE),
				Arguments.of(HyperwalletUser.LetterOfAuthorizationStatus.FAILED, Boolean.TRUE));
	}

	private void mockProgramsConfiguration() {
		lenient().when(hyperwalletProgramsConfigurationMock.getAllProgramConfigurations())
				.thenReturn(List.of(hyperwalletProgramConfiguration1Mock, hyperwalletProgramConfiguration2Mock));
		lenient().when(hyperwalletProgramConfiguration1Mock.getProgramName()).thenReturn(DEFAULT_PROGRAM);
		lenient().when(hyperwalletProgramConfiguration1Mock.getUsersProgramToken()).thenReturn(DEFAULT_TOKEN);
		lenient().when(hyperwalletProgramConfiguration2Mock.getProgramName()).thenReturn(UK_PROGRAM);
		lenient().when(hyperwalletProgramConfiguration2Mock.getUsersProgramToken()).thenReturn(UK_TOKEN);
	}

}
