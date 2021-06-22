package com.paypal.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SpringFoxConfigTest {

	@InjectMocks
	private SpringFoxConfig testObj;

	@Test
	void api_shouldReturnAnObjectSetupForOAS30() {
		final Docket result = testObj.api();

		assertThat(result.getDocumentationType()).isEqualTo(DocumentationType.OAS_30);
	}

}
