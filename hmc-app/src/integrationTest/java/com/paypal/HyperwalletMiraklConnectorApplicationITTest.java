package com.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource({ "classpath:application-test.properties" })
@SpringBootTest
class HyperwalletMiraklConnectorApplicationITTest {

	@Test
	void context_shouldStart() {
		assertTrue(true);
	}

}
