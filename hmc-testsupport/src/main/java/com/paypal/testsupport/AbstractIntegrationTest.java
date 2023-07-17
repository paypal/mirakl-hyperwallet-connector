package com.paypal.testsupport;

import org.mockserver.springtest.MockServerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = IntegrationTestConfig.class)
@MockServerTest("server.url=http://localhost:${mockServerPort}/api")
@TestPropertySource({ "classpath:application-test.properties" })
@SuppressWarnings("java:S1610")
public abstract class AbstractIntegrationTest {

}
