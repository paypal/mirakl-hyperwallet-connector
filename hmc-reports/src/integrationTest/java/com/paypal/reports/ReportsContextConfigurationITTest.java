package com.paypal.reports;

import com.braintreegateway.BraintreeGateway;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ReportsContextConfigurationITTest extends AbstractIntegrationTest {

	@Autowired
	private BraintreeGateway braintreeGateway;

	@Test
	void context_shouldStart_withDefault_NotConfiguredProperties() {
		assertThat(braintreeGateway.getConfiguration().getPrivateKey()).isEqualTo("not-set");
		assertThat(braintreeGateway.getConfiguration().getPublicKey()).isEqualTo("not-set");
		assertThat(braintreeGateway.getConfiguration().getMerchantPath()).isEqualTo("/merchants/not-set");
	}

}
