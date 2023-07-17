package com.paypal.reports;

import com.braintreegateway.BraintreeGateway;
import com.paypal.reports.configuration.ReportsBraintreeApiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportsConfiguration {

	@Bean
	BraintreeGateway getBraintreeSDKClient(final ReportsBraintreeApiConfig reportsBraintreeApiConfig) {
		return new BraintreeGateway(reportsBraintreeApiConfig.getEnvironment(),
				reportsBraintreeApiConfig.getMerchantId(), reportsBraintreeApiConfig.getPublicKey(),
				reportsBraintreeApiConfig.getPrivateKey());
	}

}
