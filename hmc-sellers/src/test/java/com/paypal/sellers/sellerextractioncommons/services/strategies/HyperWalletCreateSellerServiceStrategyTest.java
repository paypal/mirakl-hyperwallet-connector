package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HyperWalletCreateSellerServiceStrategyTest extends HyperwalletSellerServiceStrategyTest {

	@Spy
	@InjectMocks
	private HyperWalletCreateSellerServiceStrategy testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Test
	void pushToHyperwallet_shouldCreateUserInHyperwallet() {
		prepareHyperwalletSDKInstance();
		when(hyperwalletMock.createUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);

		final HyperwalletUser result = testObj.pushToHyperwallet(hyperwalletUserRequestMock);

		verify(hyperwalletMock).createUser(hyperwalletUserRequestMock);
		assertThat(result).isEqualTo(hyperwalletUserResponseMock);
	}

	@Test
	void pushToHyperwallet_shouldThrowHMCHyperwalletAPIExceptionWhenHyperwalletSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		ensureHyperwalletSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCHyperwalletAPIException.class);
	}

	@Test
	void pushToHyperwallet_shouldSendAnEmailWhenHyperwalletSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		ensureHyperwalletSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(testObj).reportError("Issue detected when creating seller in Hyperwallet",
				(ERROR_MESSAGE_PREFIX + "Seller not created with clientId [%s]%n%s").formatted(CLIENT_USER_ID,
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException)));
	}

	@Test
	void pushToHyperwallet_shouldLogTheExceptionWhenHyperwalletSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		ensureHyperwalletSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCHyperwalletAPIException.class);

		verify(testObj).logErrors(
				eq("Error creating seller in hyperwallet with clientUserId [%s].%n%s".formatted(CLIENT_USER_ID,
						HyperwalletLoggingErrorsUtil.stringify(hyperwalletException))),
				eq(hyperwalletException), any(Logger.class));
	}

	@Test
	void pushToHyperwallet_shouldUpdateTheTokenInMiraklAfterAnUserIsSuccessfullyCreatedInHw() {
		prepareHyperwalletSDKInstance();
		when(hyperwalletMock.createUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);

		testObj.pushToHyperwallet(hyperwalletUserRequestMock);

		verify(miraklSellersExtractServiceMock).updateUserToken(hyperwalletUserResponseMock);
	}

	@Test
	void pushToHyperwallet_shouldThrowHMCMiraklAPIExceptionWhenMiraklSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		when(hyperwalletMock.createUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);
		ensureMiraklSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCMiraklAPIException.class);
	}

	@Test
	void pushToHyperwallet_shouldSendAnEmailWhenMiraklSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		when(hyperwalletMock.createUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);
		ensureMiraklSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCMiraklAPIException.class);

		verify(testObj).reportError("Issue detected when updating seller in Mirakl",
				(ERROR_MESSAGE_PREFIX + "Seller token not updated with clientId [%s]%n%s").formatted(CLIENT_USER_ID,
						MiraklLoggingErrorsUtil.stringify(miraklException)));
	}

	@Test
	void pushToHyperwallet_shouldLogTheExceptionWhenMiraklSDKFails() {
		doNothing().when(testObj).reportError(anyString(), anyString());
		prepareHyperwalletSDKInstance();
		when(hyperwalletMock.createUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);
		ensureMiraklSDKThrowsAnHMCException();

		assertThatThrownBy(() -> testObj.pushToHyperwallet(hyperwalletUserRequestMock))
				.isInstanceOf(HMCMiraklAPIException.class);

		verify(testObj).logErrors(
				eq("Error updating token in mirakl with clientUserId [%s]: [{}]".formatted(CLIENT_USER_ID)),
				eq(miraklException), any(Logger.class));
	}

	@Test
	void isApplicable_shouldReturnTrueWhenTokenIsNull() {
		when(sellerModelMock.getToken()).thenReturn(null);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenTokenIsNull() {
		when(sellerModelMock.getToken()).thenReturn(TOKEN);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

	private void ensureHyperwalletSDKThrowsAnHMCException() {
		doThrow(hyperwalletException).when(hyperwalletMock).createUser(hyperwalletUserRequestMock);
	}

	private void ensureMiraklSDKThrowsAnHMCException() {
		doThrow(miraklException).when(miraklSellersExtractServiceMock).updateUserToken(hyperwalletUserResponseMock);
	}

}
