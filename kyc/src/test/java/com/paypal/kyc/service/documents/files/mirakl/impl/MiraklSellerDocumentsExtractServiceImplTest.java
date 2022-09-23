package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklSellerDocumentsExtractServiceImplTest {

	private static final String SHOP_ID = "shopId";

	private static final String USER_TOKEN = "usr-12345547";

	private static final String HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD = "hw-kyc-req-proof-identity-business";

	@Spy
	@InjectMocks
	private MiraklSellerDocumentsExtractServiceImpl testObj;

	@Mock
	private MiraklSellerDocumentDownloadExtractService miraklSellerDocumentDownloadExtractServiceMock;

	@Mock
	private MiraklMarketplacePlatformOperatorApiWrapper miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private Converter<Date, MiraklGetShopsRequest> miraklGetShopsRequestConverterMock;

	@Mock
	private Converter<MiraklShop, KYCDocumentSellerInfoModel> miraklShopKyCDocumentInfoModelConverterMock;

	@Mock
	private MiraklGetShopsRequest miraklGetShopsRequestMock;

	@Mock
	private MiraklShops miraklShopsResponseMock;

	@Mock
	private MiraklShop miraklShopRequiringKYCMock, miraklShopRequiringKYCWithoutTokenMock,
			miraklShopRequiringKYCWithoutValidationMock, miraklShopNonRequiringKYCMock, miraklShopMock;

	@Mock
	private KYCDocumentSellerInfoModel kycDocumentInfoModelRequiringKYCSellerMock,
			kycDocumentInfoModelRequiringKYCWithoutTokenSellerMock,
			kycDocumentInfoModelRequiringKYCWithoutValidationSellerMock,
			kycDocumentInfoModelRequiringKYCPopulatedSellerMock, kycDocumentInfoModelNonRequiringKYCSellerMock;

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

		when(kycDocumentInfoModelRequiringKYCSellerMock.isRequiresKYC()).thenReturn(true);
		when(kycDocumentInfoModelRequiringKYCSellerMock.getUserToken()).thenReturn(USER_TOKEN);
		when(kycDocumentInfoModelRequiringKYCSellerMock.hasSelectedDocumentControlFields()).thenReturn(true);
		when(kycDocumentInfoModelRequiringKYCWithoutTokenSellerMock.isRequiresKYC()).thenReturn(true);
		when(kycDocumentInfoModelRequiringKYCWithoutTokenSellerMock.hasSelectedDocumentControlFields())
				.thenReturn(true);
		when(kycDocumentInfoModelRequiringKYCWithoutValidationSellerMock.isRequiresKYC()).thenReturn(true);

		when(miraklShopsResponseMock.getShops())
				.thenReturn(List.of(miraklShopRequiringKYCMock, miraklShopRequiringKYCWithoutTokenMock,
						miraklShopRequiringKYCWithoutValidationMock, miraklShopNonRequiringKYCMock));
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopRequiringKYCMock))
				.thenReturn(kycDocumentInfoModelRequiringKYCSellerMock);
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopRequiringKYCWithoutTokenMock))
				.thenReturn(kycDocumentInfoModelRequiringKYCWithoutTokenSellerMock);
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopRequiringKYCWithoutValidationMock))
				.thenReturn(kycDocumentInfoModelRequiringKYCWithoutValidationSellerMock);
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopNonRequiringKYCMock))
				.thenReturn(kycDocumentInfoModelNonRequiringKYCSellerMock);
		when(miraklSellerDocumentDownloadExtractServiceMock
				.getDocumentsSelectedBySeller(kycDocumentInfoModelRequiringKYCSellerMock))
						.thenReturn(kycDocumentInfoModelRequiringKYCPopulatedSellerMock);

		final List<KYCDocumentSellerInfoModel> result = testObj
				.extractProofOfIdentityAndBusinessSellerDocuments(deltaMock);

		verify(miraklSellerDocumentDownloadExtractServiceMock)
				.getDocumentsSelectedBySeller(kycDocumentInfoModelRequiringKYCSellerMock);
		verify(miraklSellerDocumentDownloadExtractServiceMock, never())
				.getDocumentsSelectedBySeller(kycDocumentInfoModelNonRequiringKYCSellerMock);
		verify(miraklSellerDocumentDownloadExtractServiceMock, never())
				.getDocumentsSelectedBySeller(kycDocumentInfoModelRequiringKYCWithoutTokenSellerMock);
		verify(miraklSellerDocumentDownloadExtractServiceMock, never())
				.getDocumentsSelectedBySeller(kycDocumentInfoModelRequiringKYCWithoutValidationSellerMock);
		assertThat(result).containsExactlyInAnyOrder(kycDocumentInfoModelRequiringKYCPopulatedSellerMock);
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

		when(miraklMarketplacePlatformOperatorApiClientMock.updateShops(any(MiraklUpdateShopsRequest.class)))
				.thenReturn(miraklUpdateShopsMock);

		testObj.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(kycDocumentOne);

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

		assertThat(updatedSellerIdList).containsOnly(2000L);
		assertThat(updatedFlagValueList).containsExactly("false");
		assertThat(updatedFlagCodeList).containsExactly(HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
	}

	@Test
	void setFlagToPushSellerDocumentsToFalse_shouldSendEmailNotificationWhenMiraklExceptionIsThrown() {
		final KYCDocumentSellerInfoModel kycDocumentOne = KYCDocumentSellerInfoModel.builder().clientUserId("2000")
				.build();

		final MiraklException miraklException = new MiraklException("Something went wrong");

		doThrow(miraklException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(any(MiraklUpdateShopsRequest.class));

		assertThatThrownBy(() -> testObj.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(kycDocumentOne))
				.isInstanceOf(HMCMiraklAPIException.class);

		verify(kycMailNotificationUtilMock).sendPlainTextEmail("Issue setting push document flags to false in Mirakl",
				String.format("Something went wrong setting push document flag to false in Mirakl for Shop Id 2000%n%s",
						MiraklLoggingErrorsUtil.stringify(miraklException)));
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

	@Test
	void extractKYCSellerDocuments_shouldCallPopulateMiraklShopDocuments() {
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(Mockito.any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of(miraklShopMock));
		when(miraklShopMock.getId()).thenReturn(SHOP_ID);
		when(miraklShopKyCDocumentInfoModelConverterMock.convert(miraklShopMock))
				.thenReturn(kycDocumentInfoModelNonRequiringKYCSellerMock);

		testObj.extractKYCSellerDocuments(SHOP_ID);

		verify(miraklShopKyCDocumentInfoModelConverterMock).convert(miraklShopMock);
		verify(miraklSellerDocumentDownloadExtractServiceMock)
				.populateMiraklShopDocuments(kycDocumentInfoModelNonRequiringKYCSellerMock);
	}

	@Test
	void extractKYCSellerDocuments_shouldNotCallPopulateMiraklShopDocuments_whenShopIdDoesNotExist() {
		when(miraklMarketplacePlatformOperatorApiClientMock.getShops(Mockito.any(MiraklGetShopsRequest.class)))
				.thenReturn(miraklShopsMock);
		when(miraklShopsMock.getShops()).thenReturn(List.of());

		final KYCDocumentInfoModel result = testObj.extractKYCSellerDocuments(SHOP_ID);

		assertThat(result).isNull();
		verify(miraklShopKyCDocumentInfoModelConverterMock, never()).convert(miraklShopMock);
		verify(miraklSellerDocumentDownloadExtractServiceMock, never())
				.populateMiraklShopDocuments(isA(KYCDocumentSellerInfoModel.class));
	}

}
