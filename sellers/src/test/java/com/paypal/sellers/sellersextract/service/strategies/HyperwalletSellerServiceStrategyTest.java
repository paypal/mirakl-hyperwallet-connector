package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class HyperwalletSellerServiceStrategyTest {

	protected static final String TOKEN = "token";

	protected static final String PROGRAM_TOKEN_VALUE = "programTokenValue";

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public static final String CLIENT_USER_ID = "2000";

	@SuppressWarnings("java:S5979")
	@Mock
	protected SellerModel sellerModelMock;

	@Mock
	protected HyperwalletUser hyperwalletUserRequestMock, hyperwalletUserResponseMock;

	@Mock
	protected HyperwalletSDKService hyperwalletSDKServiceMock;

	@Mock
	protected Hyperwallet hyperwalletMock;

	protected HyperwalletException hyperwalletException = new HyperwalletException("Test exception");

	protected MiraklException miraklException = new MiraklException("Test exception");

	protected void prepareHyperwalletSDKInstance() {
		when(hyperwalletUserRequestMock.getClientUserId()).thenReturn(CLIENT_USER_ID);
		when(hyperwalletUserRequestMock.getProgramToken()).thenReturn(PROGRAM_TOKEN_VALUE);
		when(hyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN_VALUE))
				.thenReturn(hyperwalletMock);
	}

}
