package com.paypal.sellers.sellersextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiraklSellersExtractServiceImplCustomFieldsTest {

	private static final String TOKEN_VALUE = "tokenValue";

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	private MiraklSellersExtractServiceImpl testObj;

	@Mock
	private MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClientMock;

	@Mock
	private HyperwalletUser hyperwalletUserMock;

	@Mock
	private MailNotificationUtil mailNotificationUtilMock;

	@Captor
	private ArgumentCaptor<MiraklUpdateShopsRequest> miraklUpdateShopsRequestCaptor;

	@BeforeEach
	void setUp() {
		testObj = new MiraklSellersExtractServiceImpl(miraklMarketplacePlatformOperatorApiClientMock, null,
				mailNotificationUtilMock);
	}

	@DisplayName("Should Update Value for Custom Field 'hw-user-token'")
	@Test
	void updateUserToken_shouldUpdateValueForCustomFieldHwUserToken() {
		when(hyperwalletUserMock.getToken()).thenReturn(TOKEN_VALUE);
		when(hyperwalletUserMock.getClientUserId()).thenReturn("12345");

		testObj.updateUserToken(hyperwalletUserMock);

		verify(miraklMarketplacePlatformOperatorApiClientMock).updateShops(miraklUpdateShopsRequestCaptor.capture());
		final MiraklUpdateShopsRequest miraklUpdateShopsRequest = miraklUpdateShopsRequestCaptor.getValue();
		assertThat(miraklUpdateShopsRequest.getShops()).hasSize(1);
		final MiraklUpdateShop shopToUpdate = miraklUpdateShopsRequest.getShops().get(0);
		assertThat(shopToUpdate).hasFieldOrPropertyWithValue("shopId", 12345L);
		assertThat(shopToUpdate.getAdditionalFieldValues()).hasSize(1);
		final MiraklRequestAdditionalFieldValue additionalFieldValue = shopToUpdate.getAdditionalFieldValues().get(0);
		assertThat(additionalFieldValue).isInstanceOf(MiraklSimpleRequestAdditionalFieldValue.class);
		final MiraklSimpleRequestAdditionalFieldValue castedAdditionalFieldValue = (MiraklSimpleRequestAdditionalFieldValue) additionalFieldValue;
		assertThat(castedAdditionalFieldValue.getCode()).isEqualTo("hw-user-token");
		assertThat(castedAdditionalFieldValue.getValue()).isEqualTo(TOKEN_VALUE);
	}

	@Test
	void updateUserToken_shouldSendEmailNotification_whenMiraklExceptionIsThrown() {
		when(hyperwalletUserMock.getToken()).thenReturn(TOKEN_VALUE);
		when(hyperwalletUserMock.getClientUserId()).thenReturn("12345");
		final MiraklApiException miraklApiException = new MiraklApiException(
				new MiraklErrorResponseBean(1, "Something went wrong"));
		doThrow(miraklApiException).when(miraklMarketplacePlatformOperatorApiClientMock)
				.updateShops(any(MiraklUpdateShopsRequest.class));

		testObj.updateUserToken(hyperwalletUserMock);

		verify(mailNotificationUtilMock).sendPlainTextEmail(eq("Issue detected getting shop information in Mirakl"),
				eq(String.format(ERROR_MESSAGE_PREFIX + "Something went wrong getting information of shop [12345]%n%s",
						MiraklLoggingErrorsUtil.stringify(miraklApiException))));
	}

}
