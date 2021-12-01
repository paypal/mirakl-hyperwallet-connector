package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletUpdateSellerServiceStrategyTest {

	@InjectMocks
	private HyperWalletUpdateSellerServiceStrategy testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private HyperwalletUser hyperWalletUserRequestMock, hyperWalletUserResponseMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	private static final String TOKEN = "token";

	private static final String PROGRAM_TOKEN_VALUE = "programTokenValue";

	@Test
	void createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl_shouldUpdateUser() {
		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN_VALUE))
				.thenReturn(hyperwalletMock);
		when(hyperWalletUserRequestMock.getProgramToken()).thenReturn(PROGRAM_TOKEN_VALUE);
		when(hyperwalletMock.updateUser(hyperWalletUserRequestMock)).thenReturn(hyperWalletUserResponseMock);

		final HyperwalletUser result = testObj
				.createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(hyperWalletUserRequestMock);

		verify(hyperwalletMock).updateUser(hyperWalletUserRequestMock);
		assertThat(result).isEqualTo(hyperWalletUserResponseMock);
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

}
