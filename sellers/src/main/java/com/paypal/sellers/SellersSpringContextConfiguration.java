package com.paypal.sellers;

import com.paypal.sellers.infrastructure.configuration.SellersMiraklApiConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ SellersMiraklApiConfig.class })
public class SellersSpringContextConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(SellersSpringContextConfiguration.class, args);
	}

}
