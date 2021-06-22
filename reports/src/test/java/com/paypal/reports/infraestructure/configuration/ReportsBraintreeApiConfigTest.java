package com.paypal.reports.infraestructure.configuration;

import com.braintreegateway.BraintreeGateway;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportsBraintreeApiConfigTest {

	private ReportsBraintreeApiConfig testObj;

	private static final String MERCHANT_ID = "MERCHANT_ID";

	private static final String PRIVATE_KEY = "PRIVATE_KEY";

	private static final String PUBLIC_KEY = "PUBLIC_KEY";

	private static final String ENVIRONMENT = "development";

	@BeforeEach
	void setUp() {
		testObj = new ReportsBraintreeApiConfig();
		testObj.setEnvironment(ENVIRONMENT);
		testObj.setMerchantId(MERCHANT_ID);
		testObj.setPrivateKey(PRIVATE_KEY);
		testObj.setPublicKey(PUBLIC_KEY);
	}

	@Test
	void getBraintreeSDKClient_shouldReturnBraintreeGatewayWithCorrectParameters() {
		final BraintreeGateway result = testObj.getBraintreeSDKClient();

		Assertions.assertThat(result.getConfiguration().getEnvironment().getEnvironmentName()).isEqualTo(ENVIRONMENT);
		Assertions.assertThat(result.getConfiguration().getPrivateKey()).isEqualTo(PRIVATE_KEY);
		Assertions.assertThat(result.getConfiguration().getPublicKey()).isEqualTo(PUBLIC_KEY);
		Assertions.assertThat(result.getConfiguration().getMerchantPath()).contains(MERCHANT_ID);
	}

}
