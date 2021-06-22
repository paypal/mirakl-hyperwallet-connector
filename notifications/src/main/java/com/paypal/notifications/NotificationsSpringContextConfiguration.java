package com.paypal.notifications;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class })
public class NotificationsSpringContextConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(NotificationsSpringContextConfiguration.class, args);
	}

}
