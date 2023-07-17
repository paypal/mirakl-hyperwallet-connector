package com.paypal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class HyperwalletMiraklConnectorTestApplication {

	protected HyperwalletMiraklConnectorTestApplication() {
		// Required by Checkstyle Rules
	}

	public static void main(final String[] args) {
		final SpringApplication app = new SpringApplication(HyperwalletMiraklConnectorTestApplication.class);
		app.run(args).getEnvironment();
	}

}
