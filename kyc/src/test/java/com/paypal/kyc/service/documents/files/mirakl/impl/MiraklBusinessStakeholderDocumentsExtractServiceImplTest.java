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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklBusinessStakeholderDocumentsExtractServiceImplTest {

	private static final String USER_TOKEN = "usr-12345547";

	private static final String SHOP_ID = "shopId";

	private static final String BUSINESS_STAKEHOLDER_TOKEN = "businessStakeholderToken";

	private static final String BUSINESS_STAKEHOLDER_CODE = "hw-stakeholder-token-1";

	private static final String BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE = "hw-stakeholder-req-proof-identity-1";

	private static final int BUSINESS_STAKEHOLDER_NUMBER = 1;

	private static final String CLIENT_ID_1 = "clientId1";

	private static final String CLIENT_ID_2 = "clientId2";

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
	private MiraklShop miraklShopRequiringKYCMock, miraklShopNonRequiringKYCMock;

	@Mock
	private KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller,
			kycDocumentBusinessStakeholderInfoModelRequiringKYCPopulatedMockSeller,
			kycDocumentBusinessStakeholderInfoModelNonRequiringKYCMockSeller;

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
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_shouldReturnEmptyListWhenShopIdIsNull() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(null,
				List.of(BUSINESS_STAKEHOLDER_TOKEN));

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_shouldReturnEmptyListWhenRequiredVerificationBusinessStakeHoldersIsnull() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID, null);

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_shouldReturnEmptyListWhenRequiredVerificationBusinessStakeHoldersIsEmptyList() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
				Collections.emptyList());

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_shouldReturnEmptyListWhenNoShopIsRetrived() {
		final List<String> result = testObj.getKYCCustomValuesRequiredVerificationBusinessStakeholders(SHOP_ID,
				Collections.emptyList());

		assertThat(result).isEmpty();
	}

	@Test
	void getKYCCustomValuesRequiredVerificationBusinessStakeholders_shouldReturnEmptyListWhenShopHasNoCustomValues() {
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
		when(miraklShopsResponseMock.getShops())
				.thenReturn(List.of(miraklShopRequiringKYCMock, miraklShopNonRequiringKYCMock));
		when(miraklShopRequiringKYCMock.getId()).thenReturn(CLIENT_ID_1);
		when(miraklShopNonRequiringKYCMock.getId()).thenReturn(CLIENT_ID_2);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock.convert(miraklShopRequiringKYCMock,
						BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller);
		lenient()
				.when(miraklShopKYCDocumentBusinessStakeHolderInfoModelConverterMock
						.convert(miraklShopNonRequiringKYCMock, BUSINESS_STAKEHOLDER_NUMBER))
				.thenReturn(kycDocumentBusinessStakeholderInfoModelNonRequiringKYCMockSeller);

		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller.isRequiresKYC()).thenReturn(true);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller.getClientUserId()).thenReturn(CLIENT_ID_1);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller.getUserToken()).thenReturn(USER_TOKEN);
		when(kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller
				.hasSelectedDocumentsControlFieldsInBusinessStakeholder()).thenReturn(true);

		when(miraklBusinessStakeholderDocumentDownloadExtractServiceMock
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller))
								.thenReturn(kycDocumentBusinessStakeholderInfoModelRequiringKYCPopulatedMockSeller);

		final List<KYCDocumentBusinessStakeHolderInfoModel> result = testObj
				.extractBusinessStakeholderDocuments(deltaMock);

		verify(miraklBusinessStakeholderDocumentDownloadExtractServiceMock)
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelRequiringKYCMockSeller);
		verify(miraklBusinessStakeholderDocumentDownloadExtractServiceMock, never())
				.getBusinessStakeholderDocumentsSelectedBySeller(
						kycDocumentBusinessStakeholderInfoModelNonRequiringKYCMockSeller);
		assertThat(result)
				.containsExactlyInAnyOrder(kycDocumentBusinessStakeholderInfoModelRequiringKYCPopulatedMockSeller);
	}

	@Test
	void extractBusinessStakeholderDocuments_shouldReturnAnEmptyListWhenNoShopsHasBeenUpdatedSinceDelta() {
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
		final List<KYCDocumentBusinessStakeHolderInfoModel> successfullyPushedDocumentsList = List.of(kycUserOneBstOne,
				kycUserTwoBstOne);

		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdateShopsMock);

		final var result = testObj.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(
				successfullyPushedDocumentsList);

		verify(miraklMarketplacePlatformOperatorApiClientMock).updateShops(miraklUpdateShopArgumentCaptor.capture());

		final MiraklUpdateShopsRequest miraklUpdateShopRequest = miraklUpdateShopArgumentCaptor.getValue();
		final List<Long> updatedSellerIdList = miraklUpdateShopRequest.getShops().stream()
				.map(MiraklUpdateShop::getShopId).collect(Collectors.toList());

		final List<String> updatedFlagValueList = miraklUpdateShopRequest.getShops().stream()
				.map(MiraklUpdateShop::getAdditionalFieldValues).flatMap(Collection::stream)
				.filter(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class::isInstance)
				.map(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class::cast)
				.map(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue::getValue)
				.collect(Collectors.toList());

		final List<String> updatedFlagCodeList = miraklUpdateShopRequest.getShops().stream()
				.map(MiraklUpdateShop::getAdditionalFieldValues).flatMap(Collection::stream)
				.filter(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class::isInstance)
				.map(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue.class::cast)
				.map(MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue::getCode)
				.collect(Collectors.toList());

		assertThat(updatedSellerIdList).containsExactlyInAnyOrder(2000L, 2001L);
		assertThat(updatedFlagValueList).containsExactlyInAnyOrder("false", "false");
		assertThat(updatedFlagCodeList).containsExactlyInAnyOrder(BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE,
				BUSINESS_STAKEHOLDER_PROOF_IDENTITY_CODE);

		assertThat(result).hasValue(miraklUpdateShopsMock);
	}

	@Test
	void setFlagToPushSellerDocumentsToFalse_shouldDoNothingWhenAnEmptyListIsPassedAsParam() {
		testObj.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(List.of());
		verifyNoInteractions(miraklMarketplacePlatformOperatorApiClientMock);
	}

	@Test
	void setBusinessStakeholderFlagToPushBusinessStakeholderDocumentsToFalse_shouldSendEmailNotificationWhenMiraklExceptionIsThrown() {
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

}
