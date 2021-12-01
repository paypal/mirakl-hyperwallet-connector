package com.paypal.kyc.strategies.documents.flags.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategyTest {

	private static final String BUSINESS_STAKE_HOLDER_TOKEN = "businessStakeHolderToken";

	private static final String USER_TOKEN = "userToken";

	private static final String SHOP_ID = "2000";

	private static final String HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1 = "hw-stakeholder-proof-identity-type-1";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	@Spy
	@InjectMocks
	private KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategy testObj;

	@Mock
	private HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractServiceMock;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestArgumentCaptor;

	@Test
	void isApplicable_shouldReturnFalseWhenSellerIsIndividual() {
		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.profileType(HyperwalletUser.ProfileType.INDIVIDUAL)
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenSellerIsBusinessAndBusinessStakeHolderVerificationIsNotRequired() {
		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.businessStakeholderVerificationStatus(HyperwalletUser.BusinessStakeholderVerificationStatus.VERIFIED)
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenSellerIsBusinessAndBusinessStakeHolderVerificationIsEmpty() {
		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnTrueWhenSellerIsBusinessAndProofOfIdentityForBusinessStakeHolderIsRequired() {
		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.businessStakeholderVerificationStatus(HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED)
				.build();
		//@formatter:on

		final boolean result = testObj.isApplicable(kycUserDocumentFlagsNotificationBodyModel);

		assertThat(result).isTrue();
	}

	@Test
	void execute_shouldCallUpdateShopWithBusinessStakeHolderCustomValueFlags() {
		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.hyperwalletProgram(HYPERWALLET_PROGRAM)
				.userToken(USER_TOKEN)
				.clientUserId(SHOP_ID)
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.businessStakeholderVerificationStatus(HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED)
				.build();
		//@formatter:on

		when(hyperwalletBusinessStakeholderExtractServiceMock
				.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM, USER_TOKEN))
						.thenReturn(List.of(BUSINESS_STAKE_HOLDER_TOKEN));

		when(miraklBusinessStakeholderDocumentsExtractServiceMock
				.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
						List.of(BUSINESS_STAKE_HOLDER_TOKEN)))
								.thenReturn(List.of(HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1));

		testObj.execute(kycUserDocumentFlagsNotificationBodyModel);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(miraklUpdateShopsRequestArgumentCaptor.capture());
		final MiraklUpdateShopsRequest miraklRequest = miraklUpdateShopsRequestArgumentCaptor.getValue();
		assertThat(miraklRequest.getShops()).hasSize(1);
		final MiraklUpdateShop updateShop = miraklRequest.getShops().get(0);
		assertThat(updateShop.getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = updateShop.getAdditionalFieldValues();
		assertThat(additionalFieldValues).hasSize(1);
		final MiraklRequestAdditionalFieldValue additionalFieldValue = additionalFieldValues.get(0);
		assertThat(additionalFieldValue.getCode()).isEqualTo(HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1);
		assertThat(additionalFieldValue)
				.isInstanceOf(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class);
		assertThat(((MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue)
				.getValue()).isEqualTo(Boolean.TRUE.toString());
	}

	@Test
	void execute_shouldNotCallUpdateShopWhenNoBusinessStakeHoldersRequiresVerification() {
		//@formatter:off
		KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
				.builder()
				.hyperwalletProgram(HYPERWALLET_PROGRAM)
				.userToken(USER_TOKEN)
				.clientUserId(SHOP_ID)
				.profileType(HyperwalletUser.ProfileType.BUSINESS)
				.businessStakeholderVerificationStatus(HyperwalletUser.BusinessStakeholderVerificationStatus.REQUIRED)
				.build();
		//@formatter:on

		when(hyperwalletBusinessStakeholderExtractServiceMock
				.getKYCRequiredVerificationBusinessStakeHolders(HYPERWALLET_PROGRAM, USER_TOKEN)).thenReturn(List.of());

		testObj.execute(kycUserDocumentFlagsNotificationBodyModel);

		verifyNoInteractions(miraklMarketplacePlatformOperatorApiClientMock);
	}

}
