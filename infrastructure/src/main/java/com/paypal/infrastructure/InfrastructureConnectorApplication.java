package com.paypal.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InfrastructureConnectorApplication {

	public static void main(final String[] args) {
		SpringApplication.run(InfrastructureConnectorApplication.class, args);
	}

}
