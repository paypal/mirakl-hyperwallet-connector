package com.paypal.sellers.stakeholdersextraction.services;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessStakeholderTokenUpdateServiceImplTest {

	private static final String BUSINESS_STAKE_HOLDER_TOKEN = "businessStakeHolderToken";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	@InjectMocks
	private BusinessStakeholderTokenUpdateServiceImpl testObj;

	@Mock
	private MiraklClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private BusinessStakeHolderModel businessStakeHolderModelMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestCaptor;

	@BeforeEach
	void setUp() {
		testObj = new BusinessStakeholderTokenUpdateServiceImpl(miraklMarketplacePlatformOperatorApiClientMock,
				mailNotificationUtilMock);
	}

	@DisplayName("Should Update Value for Custom Field 'hw-user-token'")
	@Test
	void updateBusinessStakeholderToken_shouldUpdateValueForCustomFieldHwUserToken() {
		when(sellerModelMock.getBusinessStakeHolderDetails()).thenReturn(List.of(businessStakeHolderModelMock));
		when(sellerModelMock.getClientUserId()).thenReturn("12345");

		when(businessStakeHolderModelMock.getToken()).thenReturn(BUSINESS_STAKE_HOLDER_TOKEN);
		when(businessStakeHolderModelMock.getStkId()).thenReturn(1);

		testObj.updateBusinessStakeholderToken(sellerModelMock.getClientUserId(),
				sellerModelMock.getBusinessStakeHolderDetails());

		verify(miraklMarketplacePlatformOperatorApiClientMock).updateShops(miraklUpdateShopsRequestCaptor.capture());
		final MiraklUpdateShopsRequest miraklUpdateShopsRequest = miraklUpdateShopsRequestCaptor.getValue();
		assertThat(miraklUpdateShopsRequest.getShops()).hasSize(1);
		final MiraklUpdateShop shopToUpdate = miraklUpdateShopsRequest.getShops().get(0);
		assertThat(shopToUpdate).hasFieldOrPropertyWithValue("shopId", 12345L);
		assertThat(shopToUpdate.getAdditionalFieldValues()).hasSize(1);
		final MiraklRequestAdditionalFieldValue additionalFieldValue = shopToUpdate.getAdditionalFieldValues().get(0);
		assertThat(additionalFieldValue).isInstanceOf(MiraklSimpleRequestAdditionalFieldValue.class);
		final MiraklSimpleRequestAdditionalFieldValue castedAdditionalFieldValue = (MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue;
		assertThat(castedAdditionalFieldValue.getCode()).isEqualTo("hw-stakeholder-token-1");
		assertThat(castedAdditionalFieldValue.getValue()).isEqualTo(BUSINESS_STAKE_HOLDER_TOKEN);
	}

	@Test
	void updateUserToken_shouldSendEmailNotification_whenMiraklExceptionIsThrown() {
		when(sellerModelMock.getClientUserId()).thenReturn("12345");
		when(sellerModelMock.getBusinessStakeHolderDetails()).thenReturn(List.of(businessStakeHolderModelMock));
		final MiraklApiException miraklApiException = new MiraklApiException(
				new MiraklErrorResponseBean(1, "Something went wrong", "correlation-id"));
		doThrow(miraklApiException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(any(MiraklUpdateShopsRequest.class));

		testObj.updateBusinessStakeholderToken(sellerModelMock.getClientUserId(),
				sellerModelMock.getBusinessStakeHolderDetails());

		verify(mailNotificationUtilMock).sendPlainTextEmail("Issue detected getting shop information in Mirakl",
				(ERROR_MESSAGE_PREFIX + "Something went wrong getting information of shop [12345]%n%s")
						.formatted(MiraklLoggingErrorsUtil.stringify(miraklApiException)));
	}

	@Test
	void updateUserToken_shouldDoNothing_whenSellerModelHasNoBusinessStakeHolders() {
		when(sellerModelMock.getBusinessStakeHolderDetails()).thenReturn(null);

		testObj.updateBusinessStakeholderToken(sellerModelMock.getClientUserId(),
				sellerModelMock.getBusinessStakeHolderDetails());

		verifyNoInteractions(mailNotificationUtilMock);
		verifyNoInteractions(miraklMarketplacePlatformOperatorApiClientMock);
	}

}
