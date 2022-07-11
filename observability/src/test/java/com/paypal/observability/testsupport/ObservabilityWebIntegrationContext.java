package com.paypal.observability.testsupport;

import com.paypal.observability.ObservabilityTestConfig;
import org.junit.jupiter.api.Tag;
import org.mockserver.springtest.MockServerTest;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag("IntegrationTest")
@SpringBootTest(classes = ObservabilityTestConfig.class)
@TestPropertySource(locations = "classpath:observability-test.properties")
@MockServerTest("server.url=http://localhost:${mockServerPort}/api")
@AutoConfigureMockMvc
@EnableAutoConfiguration
public @interface ObservabilityWebIntegrationContext {

}
