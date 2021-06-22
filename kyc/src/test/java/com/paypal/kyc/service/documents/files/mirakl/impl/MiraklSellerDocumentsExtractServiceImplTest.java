package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
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
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentDownloadExtractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklSellerDocumentsExtractServiceImplTest {

	private static final String HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD = "hw-kyc-req-proof-identity-business";

	private static final String USER_TOKEN = "usr-12345547";

	private static final String SHOP_ID = "shopId";

	@Spy
	@InjectMocks
	private MiraklSellerDocumentsExtractServiceImpl testObj;

	@Mock
	private MiraklSellerDocumentDownloadExtractService miraklSellerDocumentDownloadExtractServiceMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private Converter<Date, MiraklGetShopsRequest> miraklGetShopsRequestConverterMock;

	@Mock
	private Converter<MiraklShop, KYCDocumentSellerInfoModel> miraklShopKyCDocumentInfoModelConverterMock;

	@Mock
	private KYCBusinessStakeHolderConverter kycBusinessStakeHolderConverterMock;

	@Mock
	private MiraklGetShopsRequest miraklGetShopsRequestMock;

	@Mock
	private MiraklShops miraklShopsResponseMock;

	@Mock
	private MiraklShop miraklShopRequiringKYCMock, miraklShopNonRequiringKYCMock, miraklShopMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentInfoModelRequiringKYCMockSeller,
			kycDocumentInfoModelRequiringKYCPopulatedMockSeller, kycDocumentInfoModelNonRequiringKYCMockSeller;

	@Mock
	private Date deltaMock;

	@Mock
	private MiraklUpdatedShops miraklUpdateShopsMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopArgumentCaptor;

	@Mock
	private MailNotificationUtil kycMailNotificationUtilMock;

	@Mock
	private MiraklShops miraklShopsMock;

	@BeforeEach
	void setUp() {
		testObj = new MiraklSellerDocumentsExtractServiceImpl(miraklSellerDocumentDownloadExtractServiceMock,
				miraklGetShopsRequestConverterMock, miraklShopKyCDocumentInfoModelConverterMock,
				miraklMarketplacePlatformOperatorApiClientMock, kycMailNotificationUtilMock);
	}

	@Test
	void extractProofOfIdentityAndBusinessSellerDocuments_shouldReturnAllKycDocumentInfoForShopsFlaggedAsKYCRequiredAndExistingOnHyperwallet() {
		when(miraklGetShopsRequestConverterMock.convert(deltaMock)).thenReturn(miraklGetShopsRequestMock);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestMock))
				.thenReturn(miraklShopsResponseMock);

		when(kycDocumentInfoModelRequiringKYCMockSeller.isRequiresKYC()).thenReturn(true);
		when(kycDocumentInfoModelRequiringKYCMockSeller.getUserToken()).thenReturn(USER_TOKEN);
		when(kycDocumentInfoModelRequiringKYCMockSeller.hasSelectedDocumentControlFields()).thenReturn(true);
		when(miraklShopsResponseMock.getShops())
				.thenReturn(List.of(miraklShopRequiringKYCMock, miraklShopNonRequiringKYCMock));
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopRequiringKYCMock))
				.thenReturn(kycDocumentInfoModelRequiringKYCMockSeller);
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopNonRequiringKYCMock))
				.thenReturn(kycDocumentInfoModelNonRequiringKYCMockSeller);
		when(miraklSellerDocumentDownloadExtractServiceMock
				.getDocumentsSelectedBySeller(kycDocumentInfoModelRequiringKYCMockSeller))
						.thenReturn(kycDocumentInfoModelRequiringKYCPopulatedMockSeller);

		final List<KYCDocumentSellerInfoModel> result = testObj
				.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);

		verify(miraklSellerDocumentDownloadExtractServiceMock)
				.getDocumentsSelectedBySeller(kycDocumentInfoModelRequiringKYCMockSeller);
		verify(miraklSellerDocumentDownloadExtractServiceMock, never())
				.getDocumentsSelectedBySeller(kycDocumentInfoModelNonRequiringKYCMockSeller);
		assertThat(result).containsExactlyInAnyOrder(kycDocumentInfoModelRequiringKYCPopulatedMockSeller);
	}

	@Test
	void extractProofOfIdentityAndBusinessSellerDocuments_shouldReturnAnEmptyListWhenNoShopsHasBeenUpdatedSinceDelta() {
		when(miraklGetShopsRequestConverterMock.convert(deltaMock)).thenReturn(miraklGetShopsRequestMock);
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(miraklGetShopsRequestMock))
				.thenReturn(miraklShopsResponseMock);
		when(miraklShopsResponseMock.getShops()).thenReturn(null);

		final List<KYCDocumentSellerInfoModel> result = testObj
				.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);

		assertThat(result).isEmpty();
	}

	@Test
	void setFlagToPushSellerDocumentsToFalse_shouldCallMiraklWithTheClientUsersIdPassedAsParam() {
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder().clientUserId("2000")
				.build();
		final KYCDocumentSellerInfoModel kycDocumentTwo = KYCDocumentSellerInfoModel.builder().clientUserId("2001")
				.build();
		final List<KYCDocumentSellerInfoModel> successfullyPushedDocumentsList = List.of(kycDocumentOne,
				kycDocumentTwo);

		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdateShopsMock);

		final var result = testObj
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(successfullyPushedDocumentsList);

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
		assertThat(updatedFlagCodeList).containsExactlyInAnyOrder(
				HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD,
				HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);

		assertThat(result).hasValue(miraklUpdateShopsMock);
	}

	@Test
	void setFlagToPushSellerDocumentsToFalse_shouldDoNothingWhenAnEmptyListIsPassedAsParam() {
		testObj.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(List.of());
		verifyNoInteractions(miraklMarketplacePlatformOperatorApiClientMock);
	}

	@Test
	void setFlagToPushSellerDocumentsToFalse_shouldSendEmailNotificationWhenMiraklExceptionIsThrown() {
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder().clientUserId("2000")
				.build();
		final List<KYCDocumentSellerInfoModel> successfullyPushedDocumentsList = List.of(kycDocumentOne);

		final MiraklException miraklException = new MiraklException("Something went wrong");

		doThrow(miraklException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(any(MiraklUpdateShopsRequest.class));

		testObj.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(successfullyPushedDocumentsList);

		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue setting push document flags to false in Mirakl",
				String.format(
						"Something went wrong setting push document flag to false in Mirakl for shop Id [2000]%n%s",
						String.join(",", MiraklLoggingErrorsUtil.stringify(miraklException))));
	}

	@Test
	void extractMiraklShop_shouldMiraklShopWhenItExistsInMirakl() {
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(Mockito.any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShopMock));
		when(miraklShopMock.getId()).thenReturn(SHOP_ID);

		final Optional<MiraklShop> result = testObj.extractMiraklShop(SHOP_ID);

		assertThat(result).isPresent().contains(miraklShopMock);
	}

}
