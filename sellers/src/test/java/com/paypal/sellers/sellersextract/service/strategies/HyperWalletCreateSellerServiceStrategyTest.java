package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperWalletCreateSellerServiceStrategyTest {

	private static final String TOKEN = "token";

	private static final String PROGRAM_TOKEN_VALUE = "programTokenValue";

	@InjectMocks
	private HyperWalletCreateSellerServiceStrategy testObj;

	@Mock
	private SellerModel sellerModelMock;

	@Mock
	private HyperwalletUser hyperwalletUserRequestMock, hyperwalletUserResponseMock;

	@Mock
	private HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	private Hyperwallet hyperwalletMock;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Test
	void callMiraklAPI() {
		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN_VALUE))
				.thenReturn(hyperwalletMock);
		when(hyperwalletMock.createUser(hyperwalletUserRequestMock)).thenReturn(hyperwalletUserResponseMock);
		when(hyperwalletUserRequestMock.getProgramToken()).thenReturn(PROGRAM_TOKEN_VALUE);
		final HyperwalletUser result = testObj.callMiraklAPI(hyperwalletUserRequestMock);

		verify(hyperwalletMock).createUser(hyperwalletUserRequestMock);
		verify(miraklSellersExtractServiceMock).updateUserToken(hyperwalletUserResponseMock);
		assertThat(result).isEqualTo(hyperwalletUserResponseMock);
	}

	@Test
	void isApplicable_shouldReturnTrueWhenTokenIsNull() {
		Mockito.when(sellerModelMock.getToken()).thenReturn(null);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isTrue();
	}

	@Test
	void isApplicable_shouldReturnFalseWhenTokenIsNull() {
		Mockito.when(sellerModelMock.getToken()).thenReturn(TOKEN);

		final boolean result = testObj.isApplicable(sellerModelMock);

		assertThat(result).isFalse();
	}

}
