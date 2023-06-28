package com.paypal.testsupport;

import com.paypal.testsupport.mocks.MockServerExpectationsLoader;
import com.paypal.testsupport.mocks.hyperwallet.BusinessStakeHoldersEndpointMock;
import com.paypal.testsupport.mocks.hyperwallet.PaymentsEndpointMock;
import com.paypal.testsupport.mocks.hyperwallet.UsersEndpointMock;
import com.paypal.testsupport.mocks.mirakl.MiraklShopsDocumentsEndpointMock;
import com.paypal.testsupport.mocks.mirakl.MiraklShopsEndpointMock;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;

public abstract class AbstractMockEnabledIntegrationTest extends AbstractIntegrationTest {

	protected MockServerClient mockServerClient;

	protected PaymentsEndpointMock paymentsEndpointMock;

	protected BusinessStakeHoldersEndpointMock businessStakeHoldersEndpointMock;

	protected UsersEndpointMock usersEndpointMock;

	protected MiraklShopsEndpointMock miraklShopsEndpointMock;

	protected MiraklShopsDocumentsEndpointMock miraklShopsDocumentsEndpointMock;

	protected MockServerExpectationsLoader mockServerExpectationsLoader;

	@BeforeEach
	public void setMockEndpoints() {
		paymentsEndpointMock = new PaymentsEndpointMock(mockServerClient);
		businessStakeHoldersEndpointMock = new BusinessStakeHoldersEndpointMock(mockServerClient);
		usersEndpointMock = new UsersEndpointMock(mockServerClient);
		miraklShopsEndpointMock = new MiraklShopsEndpointMock(mockServerClient);
		miraklShopsDocumentsEndpointMock = new MiraklShopsDocumentsEndpointMock(mockServerClient);
		mockServerExpectationsLoader = new MockServerExpectationsLoader(mockServerClient);
	}

}
