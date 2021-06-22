package com.paypal.kyc.strategies.status;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCRejectionReasonTypeEnum;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCRejectionReasonService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD;
import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractKYCUserStatusNotificationStrategyTest {

	private static final String SHOP_ID = "2000";

	@Spy
	@InjectMocks
	private MyAbstractKYCUserStatusNotificationStrategy testObj;

	@Mock
	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModelMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private MiraklUpdatedShops miraklUpdatedShopResponseMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private KYCRejectionReasonService kycRejectionReasonServiceMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopMockArgumentCaptor;

	@Test
	void updateShop_shouldCallMiraklApiWithValuesProvidedInNotificationBody() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdatedShopResponseMock);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.APPROVED);

		when(kycRejectionReasonServiceMock.getRejectionReasonDescriptions(Mockito.anyList()))
				.thenReturn(StringUtils.EMPTY);

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(miraklUpdateShopMockArgumentCaptor.capture());
		final var request = miraklUpdateShopMockArgumentCaptor.getValue();
		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		assertThat(request.getShops().get(0).getKyc().getStatus()).isEqualTo(MiraklShopKycStatus.APPROVED);
		assertThat(request.getShops().get(0).getAdditionalFieldValues()).isNull();
	}

	@Test
	void updateShop_shouldCallMiraklApiWithValuesProvidedModifyingKYCVerificationStatus() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(kycUserStatusNotificationBodyModelMock.getVerificationStatus())
				.thenReturn(HyperwalletUser.VerificationStatus.REQUIRED);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdatedShopResponseMock);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.REFUSED);

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(miraklUpdateShopMockArgumentCaptor.capture());
		final var request = miraklUpdateShopMockArgumentCaptor.getValue();
		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		assertThat(request.getShops().get(0).getKyc().getStatus()).isEqualTo(MiraklShopKycStatus.REFUSED);
		final MiraklRequestAdditionalFieldValue additionalFieldValue = request.getShops().get(0)
				.getAdditionalFieldValues().get(0);
		final var castedAdditionalFieldValue = (MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue;
		assertThat(castedAdditionalFieldValue.getCode())
				.isEqualTo(HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
		assertThat(castedAdditionalFieldValue.getValue()).isEqualTo("true");
	}

	@Test
	void updateShop_shouldCallMiraklApiModifyingKYCLetterOfAuthorization() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(kycUserStatusNotificationBodyModelMock.getReasonsType())
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));
		when(kycUserStatusNotificationBodyModelMock.getLetterOfAuthorizationStatus())
				.thenReturn(HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdatedShopResponseMock);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.REFUSED);
		when(kycRejectionReasonServiceMock
				.getRejectionReasonDescriptions(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED)))
						.thenReturn(KYCRejectionReasonTypeEnum.getReasonHeader()
								+ KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED.getReason()
								+ KYCRejectionReasonTypeEnum.getReasonFooter());

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		verify(kycRejectionReasonServiceMock)
				.getRejectionReasonDescriptions(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));
		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(miraklUpdateShopMockArgumentCaptor.capture());
		final var request = miraklUpdateShopMockArgumentCaptor.getValue();
		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		final MiraklRequestAdditionalFieldValue additionalFieldValue = request.getShops().get(0)
				.getAdditionalFieldValues().get(0);
		final var castedAdditionalFieldValue = (MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue;
		assertThat(castedAdditionalFieldValue.getCode())
				.isEqualTo(HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD);
		assertThat(castedAdditionalFieldValue.getValue()).isEqualTo("true");
	}

	@Test
	void updateShop_shouldCallMiraklApiModifyingKYCLetterOfAuthorizationAndVerificationStatus() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(kycUserStatusNotificationBodyModelMock.getReasonsType())
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));
		when(kycUserStatusNotificationBodyModelMock.getVerificationStatus())
				.thenReturn(HyperwalletUser.VerificationStatus.REQUIRED);
		when(kycUserStatusNotificationBodyModelMock.getLetterOfAuthorizationStatus())
				.thenReturn(HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdatedShopResponseMock);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.REFUSED);

		when(kycRejectionReasonServiceMock
				.getRejectionReasonDescriptions(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED)))
						.thenReturn(KYCRejectionReasonTypeEnum.getReasonHeader()
								+ KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED.getReason()
								+ KYCRejectionReasonTypeEnum.getReasonFooter());

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		verify(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(miraklUpdateShopMockArgumentCaptor.capture());
		final var request = miraklUpdateShopMockArgumentCaptor.getValue();

		verify(kycRejectionReasonServiceMock)
				.getRejectionReasonDescriptions(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));

		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		final List<MiraklRequestAdditionalFieldValue> additionalFieldValue = request.getShops().get(0)
				.getAdditionalFieldValues();

		assertThat(additionalFieldValue).hasSize(2);
		final List<String> additionalCodes = additionalFieldValue.stream()
				.map(MiraklRequestAdditionalFieldValue::getCode).collect(Collectors.toList());

		assertThat(additionalCodes).containsExactlyInAnyOrder(
				HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD,
				HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
	}

	@Test
	void updateShop_shouldLogTheErrorAndSendAnEmailInCaseMiraklAPIFails() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		final var exception = new MiraklApiException(new MiraklErrorResponseBean(100, "Something went wrong"));
		doThrow(exception).when(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(any(MiraklUpdateShopsRequest.class));
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.APPROVED);

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
				String.format(
						"There was an error, please check the logs for further information:\n"
								+ "Something went wrong updating KYC information of shop [%s]%n%s",
						SHOP_ID, MiraklLoggingErrorsUtil.stringify(exception)));
	}

	@Test
	void execute_shouldCallExpectedMiraklStatusAndUpdatedShopWithProvidedParams() {
		doReturn(Optional.empty()).when(testObj).updateShop(kycUserStatusNotificationBodyModelMock);

		testObj.execute(kycUserStatusNotificationBodyModelMock);

		verify(testObj).updateShop(kycUserStatusNotificationBodyModelMock);
	}

	private static class MyAbstractKYCUserStatusNotificationStrategy extends AbstractKYCUserStatusNotificationStrategy {

		public MyAbstractKYCUserStatusNotificationStrategy(
				final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
				final MailNotificationUtil mailNotificationUtil,
				final KYCRejectionReasonService kycRejectionReasonService) {
			super(miraklOperatorClient, mailNotificationUtil, kycRejectionReasonService);
		}

		@Override
		protected MiraklShopKycStatus expectedKycMiraklStatus(
				final KYCUserStatusNotificationBodyModel incomingNotification) {
			return null;
		}

		@Override
		public boolean isApplicable(final KYCUserStatusNotificationBodyModel source) {
			return false;
		}

	}

}
