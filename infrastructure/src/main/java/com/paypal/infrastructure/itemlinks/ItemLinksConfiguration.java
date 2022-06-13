package com.paypal.infrastructure.itemlinks;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = { "com.paypal.infrastructure.itemlinks" },
		entityManagerFactoryRef = "applicationEntityManagerFactory",
		transactionManagerRef = "applicationTransactionManager")
@ComponentScan
public class ItemLinksConfiguration {

}
