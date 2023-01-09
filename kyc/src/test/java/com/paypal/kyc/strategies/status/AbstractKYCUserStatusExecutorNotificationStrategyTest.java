package com.paypal.kyc.strategies.status;

import com.callibrity.logging.test.LogTrackerStub;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.domain.common.error.ErrorBean;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShopKyc;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShopWithErrors;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShopReturn;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.*;
import com.paypal.kyc.service.KYCRejectionReasonService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD;
import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractKYCUserStatusExecutorNotificationStrategyTest {

	private static final String SHOP_ID = "2000";

	private static final String ERROR_BEAN_MESSAGE = "errorBeanMessage";

	private static final String MIRAKL_CUSTOM_FIELD_NAME_1 = "miraklCustomFieldName1";

	private static final String MIRAKL_CUSTOM_FIELD_NAME_2 = "miraklCustomFieldName2";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	@Spy
	@InjectMocks
	private MyAbstractKYCUserStatusNotificationStrategy testObj;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Mock
	private KYCRejectionReasonService kycRejectionReasonServiceMock;

	@Mock
	private MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractServiceMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverterMock;

	@Mock
	private ErrorBean errorBeanMock;

	@Mock
	private MiraklShop miraklShopMock;

	@Mock
	private MiraklShopKyc miraklShopKycMock;

	@Mock
	private KYCDocumentInfoModel kycDocumentInfoModelMock;

	@Mock
	private MiraklUpdatedShops miraklUpdatedShopResponseMock;

	@Mock
	private MiraklUpdatedShopReturn miraklUpdatedShopReturnMock;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private MiraklUpdateShopWithErrors miraklUpdateShopWithErrorsMock;

	@Mock
	private MiraklShopDocument miraklShopDocumentOneMock, miraklShopDocumentTwoMock;

	@Mock
	private KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModelMock;

	@Mock
	private KYCDocumentNotificationModel notificationDocumentOneMock, notificationDocumentTwoMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopMockArgumentCaptor;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create()
			.recordForType(AbstractKYCUserStatusNotificationStrategy.class);

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
		final MiraklUpdateShopsRequest request = miraklUpdateShopMockArgumentCaptor.getValue();
		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		assertThat(request.getShops().get(0).getKyc().getStatus()).isEqualTo(MiraklShopKycStatus.APPROVED);
		assertThat(request.getShops().get(0).getAdditionalFieldValues()).isNull();
	}

	@Test
	void updateShop_shouldCallMiraklApiWithValuesProvidedModifyingKYCVerificationStatus() {
		when(testObj.isKycAutomated()).thenReturn(true);
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
		final MiraklUpdateShopsRequest request = miraklUpdateShopMockArgumentCaptor.getValue();
		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		assertThat(request.getShops().get(0).getKyc().getStatus()).isEqualTo(MiraklShopKycStatus.REFUSED);
		final MiraklRequestAdditionalFieldValue additionalFieldValue = request.getShops().get(0)
				.getAdditionalFieldValues().get(0);
		final MiraklSimpleRequestAdditionalFieldValue castedAdditionalFieldValue = (MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue;
		assertThat(castedAdditionalFieldValue.getCode())
				.isEqualTo(HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
		assertThat(castedAdditionalFieldValue.getValue()).isEqualTo("true");
	}

	@Test
	void updateShop_shouldCallMiraklApiModifyingKYCLetterOfAuthorization() {
		when(testObj.isKycAutomated()).thenReturn(true);
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
		final MiraklUpdateShopsRequest request = miraklUpdateShopMockArgumentCaptor.getValue();
		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		final MiraklRequestAdditionalFieldValue additionalFieldValue = request.getShops().get(0)
				.getAdditionalFieldValues().get(0);
		final MiraklSimpleRequestAdditionalFieldValue castedAdditionalFieldValue = (MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue;
		assertThat(castedAdditionalFieldValue.getCode())
				.isEqualTo(HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD);
		assertThat(castedAdditionalFieldValue.getValue()).isEqualTo("true");
	}

	@Test
	void updateShop_whenIsKycAutomatedFlow_shouldCallMiraklApiModifyingKYCLetterOfAuthorizationAndVerificationStatus() {
		when(testObj.isKycAutomated()).thenReturn(true);
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
		final MiraklUpdateShopsRequest request = miraklUpdateShopMockArgumentCaptor.getValue();

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
	void updateShop_whenIsKycManualFlow_shouldCallMiraklApiModifyingKYCWithoutUpdatingLetterOfAuthorizationAndVerificationStatus() {
		when(testObj.isKycAutomated()).thenReturn(false);

		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(kycUserStatusNotificationBodyModelMock.getReasonsType())
				.thenReturn(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));
		lenient().when(kycUserStatusNotificationBodyModelMock.getVerificationStatus())
				.thenReturn(HyperwalletUser.VerificationStatus.REQUIRED);
		lenient().when(kycUserStatusNotificationBodyModelMock.getLetterOfAuthorizationStatus())
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
		final MiraklUpdateShopsRequest request = miraklUpdateShopMockArgumentCaptor.getValue();

		verify(kycRejectionReasonServiceMock)
				.getRejectionReasonDescriptions(List.of(KYCRejectionReasonTypeEnum.LETTER_OF_AUTHORIZATION_REQUIRED));

		assertThat(request.getShops()).hasSize(1);
		assertThat(request.getShops().get(0).getShopId()).isEqualTo(Long.valueOf(SHOP_ID));
		final List<MiraklRequestAdditionalFieldValue> additionalFieldValue = request.getShops().get(0)
				.getAdditionalFieldValues();
		assertThat(additionalFieldValue).isNull();
	}

	@Test
	void updateShop_whenMiraklAPIReturnsAnException_shouldLogTheErrorAndSendAnEmail() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		final MiraklApiException exception = new MiraklApiException(
				new MiraklErrorResponseBean(100, "Something went wrong", "correlation-id"));
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.APPROVED);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenThrow(exception);

		final Throwable throwable = catchThrowable(() -> testObj.updateShop(kycUserStatusNotificationBodyModelMock));

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
				String.format(ERROR_MESSAGE_PREFIX + "Something went wrong updating KYC information of shop [%s]%n%s",
						SHOP_ID, MiraklLoggingErrorsUtil.stringify(exception)));
		assertThat(throwable).isEqualTo(exception);
	}

	@Test
	void updateShop_whenShopUpdateResponseIsNull_shouldLogError() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.APPROVED);

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		assertThat(logTrackerStub
				.contains(String.format("No response was received for update request for shop [%s]", SHOP_ID)))
						.isTrue();
	}

	@Test
	void updateShop_whenShopUpdateReturnsError_shouldLogTheError_andSendAnEmail_andThrowAnException() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.APPROVED);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdatedShopResponseMock);
		when(miraklUpdatedShopResponseMock.getShopReturns())
				.thenReturn(Collections.singletonList(miraklUpdatedShopReturnMock));
		when(miraklUpdatedShopReturnMock.getShopError()).thenReturn(miraklUpdateShopWithErrorsMock);
		when(miraklUpdateShopWithErrorsMock.getInput().getShopId()).thenReturn(Long.valueOf(SHOP_ID));
		when(miraklUpdateShopWithErrorsMock.getErrors()).thenReturn(Collections.singleton(errorBeanMock));
		when(errorBeanMock.toString()).thenReturn(ERROR_BEAN_MESSAGE);

		final Throwable throwable = catchThrowable(() -> testObj.updateShop(kycUserStatusNotificationBodyModelMock));

		final String exceptionMessage = String.format("Something went wrong updating KYC information of shop [%s]%n%s",
				SHOP_ID, ERROR_BEAN_MESSAGE);
		assertThat(logTrackerStub.contains(exceptionMessage)).isTrue();
		assertThat(throwable).isInstanceOf(HMCException.class);

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
				ERROR_MESSAGE_PREFIX + exceptionMessage);
	}

	@Test
	void updateShop_whenShopUpdateReturnsUpdatedShop_shouldLogUpdate() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(testObj.expectedKycMiraklStatus(kycUserStatusNotificationBodyModelMock))
				.thenReturn(MiraklShopKycStatus.APPROVED);
		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdatedShopResponseMock);
		when(miraklUpdatedShopResponseMock.getShopReturns())
				.thenReturn(Collections.singletonList(miraklUpdatedShopReturnMock));
		when(miraklUpdatedShopReturnMock.getShopUpdated()).thenReturn(miraklShopMock);
		when(miraklShopMock.getId()).thenReturn(SHOP_ID);
		when(miraklShopMock.getKyc()).thenReturn(miraklShopKycMock);
		when(miraklShopKycMock.getStatus()).thenReturn(MiraklShopKycStatus.APPROVED);

		testObj.updateShop(kycUserStatusNotificationBodyModelMock);

		assertThat(logTrackerStub.contains(
				String.format("KYC status updated to [%s] for shop [%s]", MiraklShopKycStatus.APPROVED, SHOP_ID)))
						.isTrue();
	}

	@Test
	void execute_shouldCallExpectedMiraklStatusAndUpdatedShopWithProvidedParams() {
		doNothing().when(testObj).updateShop(kycUserStatusNotificationBodyModelMock);

		testObj.execute(kycUserStatusNotificationBodyModelMock);

		verify(testObj).updateShop(kycUserStatusNotificationBodyModelMock);
	}

	@Test
	void deleteInvalidDocuments_whenKycDocumentDoesNotContainsDocuments_shouldNotCallDeleteDocuments() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(miraklSellerDocumentsExtractServiceMock.extractKYCSellerDocuments(SHOP_ID))
				.thenReturn(kycDocumentInfoModelMock);

		testObj.deleteInvalidDocuments(kycUserStatusNotificationBodyModelMock);

		verify(miraklSellerDocumentsExtractServiceMock, never()).deleteDocuments(isA(List.class));
	}

	@Test
	void deleteInvalidDocuments_whenKycDocumentAreValid_shouldNotCallDeleteDocuments() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(miraklSellerDocumentsExtractServiceMock.extractKYCSellerDocuments(SHOP_ID))
				.thenReturn(kycDocumentInfoModelMock);
		when(notificationDocumentOneMock.getDocumentStatus()).thenReturn(KYCDocumentStatusEnum.VALID);
		when(notificationDocumentTwoMock.getDocumentStatus()).thenReturn(KYCDocumentStatusEnum.VALID);
		when(kycUserStatusNotificationBodyModelMock.getDocuments())
				.thenReturn(List.of(notificationDocumentOneMock, notificationDocumentTwoMock));

		testObj.deleteInvalidDocuments(kycUserStatusNotificationBodyModelMock);

		verify(kycDocumentNotificationModelListConverterMock, never()).convert(notificationDocumentOneMock);
		verify(kycDocumentNotificationModelListConverterMock, never()).convert(notificationDocumentTwoMock);
		verify(miraklSellerDocumentsExtractServiceMock, never()).deleteDocuments(isA(List.class));
	}

	@Test
	void deleteInvalidDocuments_whenOneKycDocumentIsInvalid_shouldCallDeleteDocuments() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(miraklSellerDocumentsExtractServiceMock.extractKYCSellerDocuments(SHOP_ID))
				.thenReturn(kycDocumentInfoModelMock);
		when(notificationDocumentOneMock.getDocumentStatus()).thenReturn(KYCDocumentStatusEnum.INVALID);
		when(kycUserStatusNotificationBodyModelMock.getDocuments()).thenReturn(List.of(notificationDocumentOneMock));
		when(notificationDocumentOneMock.getCreatedOn()).thenReturn(LocalDateTime.MAX);
		when(kycDocumentNotificationModelListConverterMock.convert(notificationDocumentOneMock))
				.thenReturn(List.of(MIRAKL_CUSTOM_FIELD_NAME_1));
		when(kycDocumentInfoModelMock.getMiraklShopDocuments())
				.thenReturn(List.of(miraklShopDocumentOneMock, miraklShopDocumentTwoMock));
		when(miraklShopDocumentOneMock.getTypeCode()).thenReturn(MIRAKL_CUSTOM_FIELD_NAME_1);
		when(miraklShopDocumentOneMock.getDateUploaded()).thenReturn(new Date());
		when(miraklShopDocumentTwoMock.getTypeCode()).thenReturn(MIRAKL_CUSTOM_FIELD_NAME_2);

		testObj.deleteInvalidDocuments(kycUserStatusNotificationBodyModelMock);

		verify(miraklSellerDocumentsExtractServiceMock).deleteDocuments(List.of(miraklShopDocumentOneMock));
	}

	@Test
	void deleteInvalidDocuments_whenOneKycDocumentIsInvalidButThereIsANewDocumentInMirakl_shouldNotCallDeleteDocuments() {
		when(kycUserStatusNotificationBodyModelMock.getClientUserId()).thenReturn(SHOP_ID);
		when(miraklSellerDocumentsExtractServiceMock.extractKYCSellerDocuments(SHOP_ID))
				.thenReturn(kycDocumentInfoModelMock);
		when(notificationDocumentOneMock.getDocumentStatus()).thenReturn(KYCDocumentStatusEnum.INVALID);
		when(kycUserStatusNotificationBodyModelMock.getDocuments()).thenReturn(List.of(notificationDocumentOneMock));
		when(notificationDocumentOneMock.getCreatedOn()).thenReturn(LocalDateTime.MIN);
		when(kycDocumentNotificationModelListConverterMock.convert(notificationDocumentOneMock))
				.thenReturn(List.of(MIRAKL_CUSTOM_FIELD_NAME_1));
		when(kycDocumentInfoModelMock.getMiraklShopDocuments())
				.thenReturn(List.of(miraklShopDocumentOneMock, miraklShopDocumentTwoMock));
		when(miraklShopDocumentOneMock.getTypeCode()).thenReturn(MIRAKL_CUSTOM_FIELD_NAME_1);
		when(miraklShopDocumentOneMock.getDateUploaded()).thenReturn(new Date());
		when(miraklShopDocumentTwoMock.getTypeCode()).thenReturn(MIRAKL_CUSTOM_FIELD_NAME_2);

		testObj.deleteInvalidDocuments(kycUserStatusNotificationBodyModelMock);

		verify(miraklSellerDocumentsExtractServiceMock, never()).deleteDocuments(isA(List.class));
	}

	private static class MyAbstractKYCUserStatusNotificationStrategy extends AbstractKYCUserStatusNotificationStrategy {

		public MyAbstractKYCUserStatusNotificationStrategy(
				final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient,
				final MailNotificationUtil mailNotificationUtil,
				final KYCRejectionReasonService kycRejectionReasonService,
				final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
				final Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverter) {
			super(miraklOperatorClient, mailNotificationUtil, kycRejectionReasonService,
					miraklSellerDocumentsExtractService, kycDocumentNotificationModelListConverter);
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
