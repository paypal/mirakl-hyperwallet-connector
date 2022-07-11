package com.paypal.observability.miraklapichecks.actuator.converters;

import com.paypal.observability.miraklapichecks.model.MiraklAPICheck;
import com.paypal.observability.miraklapichecks.model.MiraklAPICheckStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MiraklAPIHealthCheckActuatorConverterImplTest {

	@InjectMocks
	private MiraklAPIHealthCheckActuatorConverterImpl testObj;

	@Test
	void from_ShouldConvertServiceOkResultIntoHealtObject() {
		//@formatter:off
		final MiraklAPICheck miraklAPICheck = MiraklAPICheck.builder()
				.miraklAPICheckStatus(MiraklAPICheckStatus.UP)
				.version("1.0.0")
				.location("LOCATION")
				.build();
		//@formatter:on
		final Health result = testObj.from(miraklAPICheck);

		assertThat(result.getStatus()).isEqualTo(Status.UP);
		assertThat(result.getDetails()).containsEntry("version", "1.0.0");
		assertThat(result.getDetails()).containsEntry("location", "LOCATION");
	}

	@Test
	void from_ShouldConvertServiceDownResultIntoHealtObject() {
		//@formatter:off
		final MiraklAPICheck miraklAPICheck = MiraklAPICheck.builder()
				.miraklAPICheckStatus(MiraklAPICheckStatus.DOWN)
				.error("ERROR")
				.location("LOCATION")
				.build();
		//@formatter:on
		final Health result = testObj.from(miraklAPICheck);

		assertThat(result.getStatus()).isEqualTo(Status.DOWN);
		assertThat(result.getDetails()).containsEntry("error", "ERROR");
		assertThat(result.getDetails()).containsEntry("location", "LOCATION");
	}

}
