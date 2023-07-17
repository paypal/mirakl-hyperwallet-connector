package com.paypal.observability.hyperwalletapichecks.actuator;

import com.paypal.observability.hyperwalletapichecks.actuator.converters.HyperwalletAPIHealthCheckActuatorConverter;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.services.HyperwalletHealthCheckService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HyperwalletAPIHealthCheckHealthIndicatorTest {

	@InjectMocks
	private HyperwalletAPIHealthCheckHealthIndicator testObj;

	@Mock
	private HyperwalletHealthCheckService hyperwalletHealthCheckServiceMock;

	@Mock
	private HyperwalletAPIHealthCheckActuatorConverter hyperwalletAPIHealthCheckActuatorConverterMock;

	@Mock
	private Health healthMock;

	@Mock
	private HyperwalletAPICheck hyperwalletAPICheckMock;

	@Test
	void health_ShouldCheckHealthStatus_WhenHyperwalletReturnsAnything() {
		when(hyperwalletHealthCheckServiceMock.check()).thenReturn(hyperwalletAPICheckMock);
		when(hyperwalletAPIHealthCheckActuatorConverterMock.from(hyperwalletAPICheckMock)).thenReturn(healthMock);

		final Health result = testObj.health();

		assertThat(result).isEqualTo(healthMock);
	}

}
