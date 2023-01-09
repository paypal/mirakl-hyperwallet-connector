package com.paypal.infrastructure.test;

import com.paypal.infrastructure.test.mocks.hyperwallet.BusinessStakeHoldersEndpointMock;
import com.paypal.infrastructure.test.mocks.hyperwallet.PaymentsEndpointMock;
import com.paypal.infrastructure.test.mocks.hyperwallet.UsersEndpointMock;
import com.paypal.infrastructure.test.mocks.mirakl.MiraklShopsDocumentsEndpointMock;
import com.paypal.infrastructure.test.mocks.mirakl.MiraklShopsEndpointMock;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = InfrastructureTestConfig.class)
@MockServerTest("server.url=http://localhost:${mockServerPort}/api")
@TestPropertySource({ "classpath:infrastructure-test.properties" })
public class InfrastructureIntegrationTest {

	protected MockServerClient mockServerClient;

	protected PaymentsEndpointMock paymentsEndpointMock;

	protected BusinessStakeHoldersEndpointMock businessStakeHoldersEndpointMock;

	protected UsersEndpointMock usersEndpointMock;

	protected MiraklShopsEndpointMock miraklShopsEndpointMock;

	protected MiraklShopsDocumentsEndpointMock miraklShopsDocumentsEndpointMock;

	@BeforeEach
	public void setMockEndpoints() {
		paymentsEndpointMock = new PaymentsEndpointMock(mockServerClient);
		businessStakeHoldersEndpointMock = new BusinessStakeHoldersEndpointMock(mockServerClient);
		usersEndpointMock = new UsersEndpointMock(mockServerClient);
		miraklShopsEndpointMock = new MiraklShopsEndpointMock(mockServerClient);
		miraklShopsDocumentsEndpointMock = new MiraklShopsDocumentsEndpointMock(mockServerClient);
	}

}
