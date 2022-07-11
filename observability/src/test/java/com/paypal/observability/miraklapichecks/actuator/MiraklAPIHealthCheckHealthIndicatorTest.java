package com.paypal.observability.miraklapichecks.actuator;

import com.paypal.observability.miraklapichecks.actuator.converters.MiraklAPIHealthCheckActuatorConverter;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.services.MiraklHealthCheckService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiraklAPIHealthCheckHealthIndicatorTest {

	@InjectMocks
	private MiraklAPIHealthCheckHealthIndicator testObj;

	@Mock
	private MiraklHealthCheckService miraklHealthCheckServiceMock;

	@Mock
	private MiraklAPIHealthCheckActuatorConverter miraklAPIHealthCheckActuatorConverterMock;

	@Mock
	private Health healthMock;

	@Mock
	private MiraklAPICheck miraklAPICheckMock;

	@Test
	void health_ShouldCheckHealthStatus_WhenMiraklV01ReturnsAnything() {
		when(miraklHealthCheckServiceMock.check()).thenReturn(miraklAPICheckMock);
		when(miraklAPIHealthCheckActuatorConverterMock.from(miraklAPICheckMock)).thenReturn(healthMock);

		final Health result = testObj.health();

		assertThat(result).isEqualTo(healthMock);
	}

}
