package com.paypal.testsupport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbstractMockEnabledIntegrationTestTest extends AbstractMockEnabledIntegrationTest {

	@Test
	void shouldStartIntegrationTestContext_AndCreateMocks() {
		assertNotNull(paymentsEndpointMock);
		assertNotNull(businessStakeHoldersEndpointMock);
		assertNotNull(usersEndpointMock);
		assertNotNull(miraklShopsEndpointMock);
		assertNotNull(miraklShopsDocumentsEndpointMock);
		assertNotNull(mockServerExpectationsLoader);
		assertNotNull(mockServerClient);
	}

}
