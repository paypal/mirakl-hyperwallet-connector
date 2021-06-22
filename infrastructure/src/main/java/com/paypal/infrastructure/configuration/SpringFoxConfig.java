package com.paypal.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger configuration class
 */
@Configuration
public class SpringFoxConfig {

	/**
	 * Setups Swagger
	 * @return
	 */
	@Bean
	public Docket api() {
		//@formatter:off
		return new Docket(DocumentationType.OAS_30)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.paypal.application.controllers"))
				.paths(PathSelectors.any()).build();
		//@formatter:on
	}

}
