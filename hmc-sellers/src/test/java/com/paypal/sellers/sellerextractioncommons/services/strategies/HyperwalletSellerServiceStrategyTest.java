package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
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
	protected UserHyperwalletSDKService userHyperwalletSDKServiceMock;

	@Mock
	protected Hyperwallet hyperwalletMock;

	protected final HyperwalletException hyperwalletException = new HyperwalletException("Test exception");

	protected final MiraklException miraklException = new MiraklException("Test exception");

	protected void prepareHyperwalletSDKInstance() {
		when(hyperwalletUserRequestMock.getClientUserId()).thenReturn(CLIENT_USER_ID);
		when(hyperwalletUserRequestMock.getProgramToken()).thenReturn(PROGRAM_TOKEN_VALUE);
		when(userHyperwalletSDKServiceMock.getHyperwalletInstanceByProgramToken(PROGRAM_TOKEN_VALUE))
				.thenReturn(hyperwalletMock);
	}

}
