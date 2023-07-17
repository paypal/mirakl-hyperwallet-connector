package com.paypal.kyc.incomingnotifications.services.flags;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.stakeholdersdocumentextraction.services.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.stakeholdersdocumentextraction.services.MiraklBusinessStakeholderDocumentsExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategyTest {

	private static final String SHOP_ID = "2000";

	private static final String USER_TOKEN = "userToken";

	private static final String HYPERWALLET_PROGRAM = "hyperwalletProgram";

	private static final String BUSINESS_STAKE_HOLDER_TOKEN = "businessStakeHolderToken";

	private static final String HW_STAKEHOLDER_PROOF_IDENTITY_TYPE_1 = "hw-stakeholder-proof-identity-type-1";

	private static final String EMAIL_BODY_PREFIX = "There was an error, please check the logs for further information:\n";

	private static final String MSG_ERROR = "Something went wrong updating KYC business stakeholder information of shop [%s]. Details [%s]";

	private static final LogTrackerStub LOG_TRACKER_STUB = LogTrackerStub.create()
			.recordForLevel(LogTracker.LogLevel.ERROR)
			.recordForType(KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategy.class);

	@Spy
	@InjectMocks
	private KYCUserDocumentFlagProofIdentityBusinessStakeHolderStrategy testObj;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private MiraklClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractServiceMock;

	@Mock
	private MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractServiceMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestArgumentCaptor;

	@Test
	void isApplicable_shouldReturnFalseWhenSellerIsIndividual() {
		//@formatter:off
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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

	@Test
	void execute_whenAPICallThrowsException_shouldLog_andSendMailNotifyingError_andThrowException() {
		//@formatter:off
		final KYCUserDocumentFlagsNotificationBodyModel kycUserDocumentFlagsNotificationBodyModel = KYCUserDocumentFlagsNotificationBodyModel
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

		final MiraklException miraklException = new MiraklException("An error has occurred");
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any())).thenThrow(miraklException);

		final Throwable throwable = catchThrowable(() -> testObj.execute(kycUserDocumentFlagsNotificationBodyModel));

		assertThat(throwable).isEqualTo(miraklException);
		assertThat(LOG_TRACKER_STUB.contains(MSG_ERROR.formatted(SHOP_ID, miraklException.getMessage()))).isTrue();

		verify(mailNotificationUtilMock).sendPlainTextEmail(
				"Issue detected updating KYC business stakeholder information in Mirakl",
				(EMAIL_BODY_PREFIX
						+ "Something went wrong updating KYC business stakeholder information for shop [%s]%n%s")
								.formatted(SHOP_ID, MiraklLoggingErrorsUtil.stringify(miraklException)));
	}

}
