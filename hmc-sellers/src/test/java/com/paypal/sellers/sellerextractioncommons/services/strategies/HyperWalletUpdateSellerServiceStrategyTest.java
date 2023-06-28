package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletUpdateSellerServiceStrategyTest extends HyperwalletSellerServiceStrategyTest {

	@Spy
	@InjectMocks
	private HyperWalletUpdateSellerServiceStrategy testObj;

	@Test
	void callHyperwallet_shouldUpdateUserInHyperwallet() {
		prepareHyperwalletSDKInstance();
		when(hyperwalletMock.updateUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);

		final HyperwalletUser result = testObj.pushToHyperwallet(hyperwalletUserRequestMock);

		verify(hyperwalletMock).updateUser(hyperwalletUserRequestMock);
		assertThat(result).isEqualTo(hyperwalletUserResponseMock);
	}

	@Test
	void callHyperwallet_shouldThrowHMCHyperwalletAPIExceptionWhenHyperwalletSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		ensureHyperwalletSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCHyperwalletAPIException.class);
	}

	@Test
	void callHyperwallet_shouldSendAnEmailWhenHyperwalletSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		ensureHyperwalletSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(testObj).reportError("Issue detected when updating seller in Hyperwallet",
				(ERROR_MESSAGE_PREFIX + "Seller not updated with clientId [%s]%n%s").formatted(CLIENT_USER_ID,
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void callHyperwallet_shouldLogTheExceptionWhenHyperwalletSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		ensureHyperwalletSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(testObj).logErrors(
				eq("Error updating seller in hyperwallet with clientUserId [%s].%n%s".formatted(CLIENT_USER_ID,
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException))),
				eq(hyperwalletException), any(Logger.class));
	}

	@Test
	void isApplicable_shouldReturnFalseWhenSellerTokenIsNull() {
		when(sellerModelMock.getToken()).thenReturn(null);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

	@Test
	void isApplicable_shouldReturnTrueWhenSellerTokenIsNotEmpty() {
		when(sellerModelMock.getToken()).thenReturn(TOKEN);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	private void ensureHyperwalletSDKThrowsAnHMCException() {
		doThrow(hyperwalletException).when(hyperwalletMock).updateUser(hyperwalletUserRequestMock);
	}

}
