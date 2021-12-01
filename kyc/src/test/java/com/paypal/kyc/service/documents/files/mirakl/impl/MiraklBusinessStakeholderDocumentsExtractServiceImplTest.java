package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.converter.KYCBusinessStakeHolderConverter;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentDownloadExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklBusinessStakeholderDocumentsExtractServiceImplTest {

	private static final String USER_TOKEN = "usr-12345547";

	private static final String SHOP_ID = "shopId";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	private static final String BUSINESS_STAKEHOLDER_CODE = "hw-stakeholder-token-1";

	private static final String BUSINESS_STAKEHOLDER_PROOF_AUTH_CODE = "hw-kyc-req-proof-authorization";

	private static final String BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE = "hw-stakeholder-req-proof-identity-1";

	private static final int BUSINESS_STAKEHOLDER_NUMBER = 1;

	private static final String CLIENT_ID_1 = "clientId1";

	private static final String CLIENT_ID_2 = "clientId2";

	private static final String CLIENT_ID_3 = "clientId3";

	@Spy
	@InjectMocks
	private MiraklBusinessStakeholderDocumentsExtractServiceImpl testObj;

	@Mock
	private MiraklBusinessStakeholderDocumentDownloadExtractService miraklBusinessStakeholderDocumentDownloadExtractServiceMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private Converter<Date, MiraklGetShopsRequest> miraklGetShopsRequestConverterMock;

	@Mock
	private KYCBusinessStakeHolderConverter miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock;

	@Mock
	private MiraklGetShopsRequest miraklGetShopsRequestMock;

	@Mock
	private MiraklShops miraklShopsResponseMock;

	@Mock
	private MiraklShop miraklShopRequiringKYCMock, miraklShopRequiringKYCWithoutSelectedControlFieldsMock,
			miraklShopNonRequiringKYCMock, miraklShopRequiringLOAMock, miraklShopRequiringKYCWithEmptyTokenMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock,
			kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerWithoutSelectedControlFieldsMock,
			kycDocumentBusinessStakeholderInfoModelRequiringKYCPopulatedSellerMock,
			kycDocumentBusinessStakeholderInfoModelNonRequiringKYCSellerMock,
			kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock,
			kycDocumentBusinessStakeholderInfoModelRequiringLOAPopulatedSellerMock,
			kycDocumentBusinessStakeholderInfoModelRequiringKYCWithEmptyTokenSellerMock;

	@Mock
	private Date deltaMock;

	@Mock
	private MiraklShops miraklShopsMock;

	@Mock
	private MiraklUpdatedShops miraklUpdateShopsMock;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopArgumentCaptor;

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_shouldReturnListOfCode() {
		final MiraklShop miraklShopStub = new MiraklShop();
		miraklShopStub.setId(SHOP_ID);
		miraklShopStub.setAdditionalFieldValues(List.of(new MiraklAdditionalFieldValue.MiraklStringAdditionalFieldValue(
				BUSINESS_STAKEHOLDER_CODE, BUSINESS_STAKEHOLDER_TOKEN)));

		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(Mockito.any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShopStub));

		final List<String> businessStakeholderToken = List.of(BUSINESS_STAKEHOLDER_TOKEN);

		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
				businessStakeholderToken);

		assertThat(result).containsExactly(BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE);
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_whenShopIdIsNull_shouldReturnEmptyList() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(null,
				List.of(BUSINESS_STAKEHOLDER_TOKEN));

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_whenRequiredVerificationBusinessStakeHoldersIsNull_shouldReturnEmptyList() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID, null);

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_whenRequiredVerificationBusinessStakeHoldersIsEmptyList_shouldReturnEmptyList() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
				Collections.emptyList());

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_whenNoShopIsRetrieved_shouldReturnEmptyList() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
				Collections.emptyList());

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_whenShopHasNoCustomValues_shouldReturnEmptyList() {
		final MiraklShop miraklShopStub = new MiraklShop();
		miraklShopStub.setId(SHOP_ID);

		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(Mockito.any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShopStub));

		final List<String> businessStakeholderToken = List.of(BUSINESS_STAKEHOLDER_TOKEN);

		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
				businessStakeholderToken);

		assertThat(result).isEmpty();
	}

	@Test
	void extractBusinessStakeholderDocuments_shouldReturnAllKycDocumentBusinessStakeholderInfoForShopsFlaggedAsKYCRequiredAndExistingOnHyperwallet() {
		when(miraklGetShopsRequestConverterMock.convert(deltaMock)).thenReturn(miraklGetShopsRequestMock);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestMock))
				.thenReturn(miraklShopsResponseMock);
		when(miraklShopsResponseMock.getShops()).thenReturn(List.of(miraklShopRequiringKYCMock,
				miraklShopRequiringKYCWithoutSelectedControlFieldsMock, miraklShopNonRequiringKYCMock,
				miraklShopRequiringLOAMock, miraklShopRequiringKYCWithEmptyTokenMock));
		when(miraklShopRequiringKYCMock.getId()).thenReturn(CLIENT_ID_1);
		when(miraklShopRequiringKYCWithoutSelectedControlFieldsMock.getId()).thenReturn(CLIENT_ID_2);
		when(miraklShopNonRequiringKYCMock.getId()).thenReturn(CLIENT_ID_3);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock.convert(miraklShopRequiringKYCMock,
						BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock
						.convert(miraklShopRequiringKYCWithoutSelectedControlFieldsMock, BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerWithoutSelectedControlFieldsMock);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock
						.convert(miraklShopNonRequiringKYCMock, BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelNonRequiringKYCSellerMock);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock.convert(miraklShopRequiringLOAMock,
						BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock
						.convert(miraklShopRequiringKYCWithEmptyTokenMock, BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringKYCWithEmptyTokenSellerMock);

		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock.isRequiresKYC()).thenReturn(true);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock.getClientUserId()).thenReturn(CLIENT_ID_1);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock.getUserToken()).thenReturn(USER_TOKEN);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder()).thenReturn(true);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerWithoutSelectedControlFieldsMock.isRequiresKYC())
				.thenReturn(true);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerWithoutSelectedControlFieldsMock
				.getClientUserId()).thenReturn(CLIENT_ID_2);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerWithoutSelectedControlFieldsMock
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder()).thenReturn(false);
		when(kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock.isRequiresLetterOfAuthorization())
				.thenReturn(true);
		when(kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock.getClientUserId()).thenReturn(CLIENT_ID_1);
		when(kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock.getUserToken()).thenReturn(USER_TOKEN);
		when(kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder()).thenReturn(true);

		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCWithEmptyTokenSellerMock.isRequiresKYC())
				.thenReturn(true);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCWithEmptyTokenSellerMock.getClientUserId())
				.thenReturn(CLIENT_ID_1);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCWithEmptyTokenSellerMock
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder()).thenReturn(true);

		when(miraklBusinessStakeholderDocumentDownloadExtractServiceMock
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock))
								.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringKYCPopulatedSellerMock);
		when(miraklBusinessStakeholderDocumentDownloadExtractServiceMock
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelRequiringLOASellerMock))
								.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringLOAPopulatedSellerMock);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.extractBusinessStakeholderDocuments(deltaMock);

		verify(miraklBusinessStakeholderDocumentDownloadExtractServiceMock)
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerMock);
		verify(miraklBusinessStakeholderDocumentDownloadExtractServiceMock, never())
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelRequiringKYCSellerWithoutSelectedControlFieldsMock);
		verify(miraklBusinessStakeholderDocumentDownloadExtractServiceMock, never())
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelNonRequiringKYCSellerMock);
		assertThat(result).containsExactlyInAnyOrder(
				kycDocumentBusinessStakeholderInfoModelRequiringKYCPopulatedSellerMock,
				kycDocumentBusinessStakeholderInfoModelRequiringLOAPopulatedSellerMock);
	}

	@Test
	void extractBusinessStakeholderDocuments_whenNoShopsHasBeenUpdatedSinceDelta_shouldReturnAnEmptyList() {
		when(miraklGetShopsRequestConverterMock.convert(deltaMock)).thenReturn(miraklGetShopsRequestMock);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestMock))
				.thenReturn(miraklShopsResponseMock);
		when(miraklShopsResponseMock.getShops()).thenReturn(null);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.extractBusinessStakeholderDocuments(deltaMock);

		assertThat(result).isEmpty();
	}

	@Test
	void setBusinessStakeholderFlagToPushBusinessStakeholderDocumentsToFalse_shouldCallMiraklWithTheClientUsersIdPassedAsParam() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycUserOneBstOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId("2000")
				.businessStakeholderMiraklNumber(1)
				.requiresKYC(Boolean.TRUE)
				.sentToHyperwallet(Boolean.TRUE)
				.build();
		//@formatter:on

		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycUserTwoBstOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId("2001")
				.businessStakeholderMiraklNumber(1)
				.requiresKYC(Boolean.TRUE)
				.sentToHyperwallet(Boolean.TRUE)
				.build();
		//@formatter:on

		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycUserThreeBstOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId("2002")
				.businessStakeholderMiraklNumber(1)
				.requiresKYC(Boolean.TRUE)
				.sentToHyperwallet(Boolean.TRUE)
				.requiresLetterOfAuthorization(Boolean.TRUE)
				.build();
		//@formatter:on

		final List<KYCDocumentBusinessStakeHolderInfoModel> successfullyPushedDocumentsList = List.of(kycUserOneBstOne,
				kycUserThreeBstOne, kycUserTwoBstOne);

		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdateShopsMock);

		final Optional<MiraklUpdatedShops> result = testObj
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
						successfullyPushedDocumentsList);

		verify(miraklMarketplacePlatformOperatorApiClientMock).updateShops(miraklUpdateShopArgumentCaptor.capture());

		final MiraklUpdateShopsRequest miraklUpdateShopRequest = miraklUpdateShopArgumentCaptor.getValue();
		final List<Long> updatedSellerIdList = miraklUpdateShopRequest.getShops().stream()
				.map(MiraklUpdateShop::getShopId).collect(Collectors.toList());

		assertThat(updatedSellerIdList).containsExactlyInAnyOrder(2000L, 2001L, 2002L);
		assertThat(getUpdatedFlagValuesForShop(miraklUpdateShopRequest, 2000L))
				.containsExactlyEntriesOf(Map.of(BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE, "false"));
		assertThat(getUpdatedFlagValuesForShop(miraklUpdateShopRequest, 2001L))
				.containsExactlyEntriesOf(Map.of(BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE, "false"));
		assertThat(getUpdatedFlagValuesForShop(miraklUpdateShopRequest, 2002L)).containsAllEntriesOf(Map
				.of(BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE, "false", BUSINESS_STAKEHOLDER_PROOF_AUTH_CODE, "false"));

		assertThat(result).hasValue(miraklUpdateShopsMock);
	}

	@Test
	void setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse_whenNoShopsAreNeededForUpdate_shouldDoNothing() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycUserOneBstOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId("2000")
				.businessStakeholderMiraklNumber(1)
				.sentToHyperwallet(Boolean.FALSE)
				.build();
		//@formatter:on

		final Optional<MiraklUpdatedShops> result = testObj
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
						Collections.singletonList(kycUserOneBstOne));

		assertThat(result).isEmpty();

		verifyNoInteractions(miraklMarketplacePlatformOperatorApiClientMock);
	}

	@Test
	void setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse_whenAnEmptyListIsPassedAsParam_shouldDoNothing() {
		testObj.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(List.of());

		verifyNoInteractions(miraklMarketplacePlatformOperatorApiClientMock);
	}

	@Test
	void setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse_whenMiraklExceptionIsThrown_shouldSendEmailNotification() {
		//@formatter:off
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentOne = KYCDocumentBusinessStakeHolderInfoModel.builder()
				.clientUserId("2000")
				.businessStakeholderMiraklNumber(1)
				.requiresKYC(Boolean.TRUE)
				.sentToHyperwallet(Boolean.TRUE)
				.build();
		final List<KYCDocumentBusinessStakeHolderInfoModel> successfullyPushedDocumentsList = List.of(kycDocumentOne);
		//@formatter:on

		final MiraklException miraklException = new MiraklException("Something went wrong");

		doThrow(miraklException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(any(MiraklUpdateShopsRequest.class));

		testObj.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(successfullyPushedDocumentsList);

		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue setting push document flags to false in Mirakl",
				String.format(
						"Something went wrong setting push document flag to false in Mirakl for shop Id [2000]%n%s",
						String.join(",", MiraklLoggingErrorsUtil.stringify(miraklException))));
	}

	private Map<String, String> getUpdatedFlagValuesForShop(final MiraklUpdateShopsRequest miraklUpdateShopRequest,
			final long shopId) {
		return miraklUpdateShopRequest.getShops().stream().filter(shop -> shop.getShopId().equals(shopId))
				.map(MiraklUpdateShop::getAdditionalFieldValues).flatMap(Collection::stream)
				.filter(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class::isInstance)
				.map(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class::cast)
				.collect(Collectors.toMap(MiraklRequestAdditionalFieldValue::getCode,
						MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue::getValue));
	}

}
