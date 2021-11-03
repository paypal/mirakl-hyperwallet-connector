package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.kyc.infrastructure.configuration.KYCHyperwalletApiConfig;
import com.paypal.kyc.model.KYCBusinessStakeholderStatusNotificationBodyModel;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndividualKYCBusinessStakeholderStatusExecutorNotificationStrategyTest {

	private static final String USER_TOKEN = "userToken";

	private static final String DEFAULT_TOKEN = "defaultToken";

	private static final String UK_TOKEN = "ukToken";

	private static final String MIRAKL_SHOP_ID = "0229";

	private static final String BSTK_PROOF_IDENTITY_FIELD = "bstkProofIdentityField";

	private static final String CUSTOM_FIELD_1 = "customField1";

	private static final String CUSTOM_FIELD_2 = "customField2";

	private static final String BSTK_TOKEN = "bstkToken";

	private static final String CLIENT_ID = "0229";

	private static Map<String, String> USER_STORE_TOKENS = Map.of("DEFAULT", DEFAULT_TOKEN, "UK", UK_TOKEN);

	@InjectMocks
	private IndividualKYCBusinessStakeholderStatusNotificationStrategy testObj;

	@Mock
	private HyperwalletSDKService hyperwalletSDKService;

	@Mock
	private KYCHyperwalletApiConfig kycHyperwalletApiConfigMock;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Mock
	private KYCBusinessStakeholderStatusNotificationBodyModel kycBusinessStakeholderStatusNotificationBodyModelMock;

	@Mock
	private MiraklUpdatedShops miraklUpdatedShopsMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestArgumentCaptor;

	@Test
	void isApplicable_shouldReturnTrue_whenBusinessStakeholderTypeIsIndividualAndNotificationTypeIsVerificationStatus() {
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getProfileType())
				.thenReturn(HyperwalletUser.ProfileType.INDIVIDUAL);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getHyperwalletWebhookNotificationType()).thenReturn(
				KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_VERIFICATION_STATUS + ".REQUIRED");

		final boolean result = testObj.isApplicable(kycBusinessStakeholderStatusNotificationBodyModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnTrue_whenBusinessStakeholderTypeIsIndividualAndNotificationTypeIsNotVerificationStatus() {
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getProfileType())
				.thenReturn(HyperwalletUser.ProfileType.INDIVIDUAL);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getHyperwalletWebhookNotificationType())
				.thenReturn(KYCConstants.HwWebhookNotificationType.USERS_BUSINESS_STAKEHOLDERS_CREATED);

		final boolean result = testObj.isApplicable(kycBusinessStakeholderStatusNotificationBodyModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void callHyperwalletSDKCatchingException_shouldReturnHyperwalletUser() {
		when(hyperwalletMock.getUser(USER_TOKEN)).thenReturn(hyperwalletUserMock);

		final HyperwalletUser result = testObj.callHyperwalletSDKCatchingException(hyperwalletMock, USER_TOKEN);

		assertThat(result).isEqualTo(hyperwalletUserMock);
	}

	@Test
	void callHyperwalletSDKCatchingException_shouldReturnNull_whenExceptionIsThrown() {
		when(hyperwalletMock.getUser(USER_TOKEN)).thenThrow(HyperwalletException.class);

		final HyperwalletUser hyperwalletUser = testObj.callHyperwalletSDKCatchingException(hyperwalletMock,
				USER_TOKEN);

		assertThat(hyperwalletUser).isNull();
	}

	@Test
	void getHyperWalletUser_shouldCallHyperwalletOnEachUserToken() {
		when(kycHyperwalletApiConfigMock.getUserStoreTokens()).thenReturn(USER_STORE_TOKENS);
		when(hyperwalletSDKService.getHyperwalletInstance(anyString())).thenReturn(hyperwalletMock);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(hyperwalletMock.getUser(USER_TOKEN)).thenReturn(hyperwalletUserMock);

		final HyperwalletUser result = testObj
				.getHyperWalletUser(kycBusinessStakeholderStatusNotificationBodyModelMock);

		verify(hyperwalletMock, times(2)).getUser(USER_TOKEN);
		assertThat(result).isEqualTo(hyperwalletUserMock);
	}

	@Test
	void getHyperWalletUser_shouldReturnNull_whenTheUserDoesNotExitInHyperWallet() {
		when(kycHyperwalletApiConfigMock.getUserStoreTokens()).thenReturn(USER_STORE_TOKENS);
		when(hyperwalletSDKService.getHyperwalletInstance(anyString())).thenReturn(hyperwalletMock);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(hyperwalletMock.getUser(USER_TOKEN)).thenThrow(HyperwalletException.class);

		final HyperwalletUser result = testObj
				.getHyperWalletUser(kycBusinessStakeholderStatusNotificationBodyModelMock);

		verify(hyperwalletMock, times(2)).getUser(USER_TOKEN);
		assertThat(result).isNull();
	}

	@Test
	void updateMiraklProofIdentityFlagStatus_shouldUpdateMiraklWithProofOfIdentityNeeded_whenStatusIsRequired() {
		when(miraklMarketplacePlatformOperatorApiClientMock
				.updateShops(miraklUpdateShopsRequestArgumentCaptor.capture())).thenReturn(miraklUpdatedShopsMock);

		testObj.updateMiraklProofIdentityFlagStatus(MIRAKL_SHOP_ID, BSTK_PROOF_IDENTITY_FIELD,
				HyperwalletUser.VerificationStatus.REQUIRED);

		final MiraklUpdateShopsRequest result = miraklUpdateShopsRequestArgumentCaptor.getValue();
		final List<MiraklRequestAdditionalFieldValue> additionalFieldValuesToBeChanged = result.getShops().get(0)
				.getAdditionalFieldValues();

		assertThat(additionalFieldValuesToBeChanged).hasSize(1);
		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue miraklSimpleRequestAdditionalFieldValue = (MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) additionalFieldValuesToBeChanged
				.get(0);
		assertThat(miraklSimpleRequestAdditionalFieldValue.getCode()).isEqualTo(BSTK_PROOF_IDENTITY_FIELD);
		assertThat(miraklSimpleRequestAdditionalFieldValue.getValue()).isEqualTo("true");
	}

	@Test
	void updateMiraklProofIdentityFlagStatus_shouldUpdateMiraklWithProofOfIdentityNotNeeded_whenStatusIsNotRequired() {
		when(miraklMarketplacePlatformOperatorApiClientMock
				.updateShops(miraklUpdateShopsRequestArgumentCaptor.capture())).thenReturn(miraklUpdatedShopsMock);

		testObj.updateMiraklProofIdentityFlagStatus(MIRAKL_SHOP_ID, BSTK_PROOF_IDENTITY_FIELD,
				HyperwalletUser.VerificationStatus.NOT_REQUIRED);

		final MiraklUpdateShopsRequest result = miraklUpdateShopsRequestArgumentCaptor.getValue();
		final List<MiraklRequestAdditionalFieldValue> additionalFieldValuesToBeChanged = result.getShops().get(0)
				.getAdditionalFieldValues();

		assertThat(additionalFieldValuesToBeChanged).hasSize(1);
		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue miraklSimpleRequestAdditionalFieldValue = (MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) additionalFieldValuesToBeChanged
				.get(0);
		assertThat(miraklSimpleRequestAdditionalFieldValue.getCode()).isEqualTo(BSTK_PROOF_IDENTITY_FIELD);
		assertThat(miraklSimpleRequestAdditionalFieldValue.getValue()).isEqualTo("false");
	}

	@Test
	void execute_shouldDoNothing_whenTheUserDoesNotExistOnHyperwallet() {
		when(kycHyperwalletApiConfigMock.getUserStoreTokens()).thenReturn(USER_STORE_TOKENS);
		when(hyperwalletSDKService.getHyperwalletInstance(anyString())).thenReturn(hyperwalletMock);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(hyperwalletMock.getUser(USER_TOKEN)).thenThrow(HyperwalletException.class);

		testObj.execute(kycBusinessStakeholderStatusNotificationBodyModelMock);

		verify(miraklBusinessStakeholderDocumentsExtractServiceMock, never())
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(anyString(), anyList());
	}

	@Test
	void execute_shouldNotCallMiraklToUpdate_whenTheUserExistOnHyperwalletButThereAreNoCustomFields() {
		when(kycHyperwalletApiConfigMock.getUserStoreTokens()).thenReturn(USER_STORE_TOKENS);
		when(hyperwalletSDKService.getHyperwalletInstance(anyString())).thenReturn(hyperwalletMock);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getToken()).thenReturn(BSTK_TOKEN);
		when(hyperwalletMock.getUser(USER_TOKEN)).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getClientUserId()).thenReturn(CLIENT_ID);
		when(miraklBusinessStakeholderDocumentsExtractServiceMock
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(anyString(), anyList()))
						.thenReturn(List.of());
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getVerificationStatus())
				.thenReturn(HyperwalletUser.VerificationStatus.REQUIRED);

		testObj.execute(kycBusinessStakeholderStatusNotificationBodyModelMock);

		verify(miraklMarketplacePlatformOperatorApiClientMock, never())
				.updateShops(isA(MiraklUpdateShopsRequest.class));
	}

	@Test
	void execute_shouldCallMiraklToUpdate_whenTheUserExistOnHyperwalletButThereAreCustomFields() {
		when(kycHyperwalletApiConfigMock.getUserStoreTokens()).thenReturn(USER_STORE_TOKENS);
		when(hyperwalletSDKService.getHyperwalletInstance(anyString())).thenReturn(hyperwalletMock);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getUserToken()).thenReturn(USER_TOKEN);
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getToken()).thenReturn(BSTK_TOKEN);
		when(hyperwalletMock.getUser(USER_TOKEN)).thenReturn(hyperwalletUserMock);
		when(hyperwalletUserMock.getClientUserId()).thenReturn(CLIENT_ID);
		when(miraklBusinessStakeholderDocumentsExtractServiceMock
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(anyString(), anyList()))
						.thenReturn(List.of(CUSTOM_FIELD_1, CUSTOM_FIELD_2));
		when(kycBusinessStakeholderStatusNotificationBodyModelMock.getVerificationStatus())
				.thenReturn(HyperwalletUser.VerificationStatus.REQUIRED);

		testObj.execute(kycBusinessStakeholderStatusNotificationBodyModelMock);

		verify(miraklMarketplacePlatformOperatorApiClientMock).updateShops(isA(MiraklUpdateShopsRequest.class));
	}

}
