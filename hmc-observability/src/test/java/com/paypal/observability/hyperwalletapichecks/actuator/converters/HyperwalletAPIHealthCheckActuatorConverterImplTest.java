package com.paypal.observability.hyperwalletapichecks.actuator.converters;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheckStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HyperwalletAPIHealthCheckActuatorConverterImplTest {

	@InjectMocks
	private HyperwalletAPIHealthCheckActuatorConverterImpl testObj;

	@Test
	void from_ShouldConvertServiceOkResultIntoHealtObject() {
		//@formatter:off
		final HyperwalletAPICheck hyperwalletAPICheck = HyperwalletAPICheck.builder()
				.hyperwalletAPICheckStatus(HyperwalletAPICheckStatus.UP)
				.location("LOCATION")
				.build();
		//@formatter:on
		final Health result = testObj.from(hyperwalletAPICheck);

		assertThat(result.getStatus()).isEqualTo(Status.UP);
		assertThat(result.getDetails()).containsEntry("location", "LOCATION");
	}

	@Test
	void from_ShouldConvertServiceDownResultIntoHealthObject() {
		//@formatter:off
		final HyperwalletAPICheck hyperwalletAPICheck = HyperwalletAPICheck.builder()
				.hyperwalletAPICheckStatus(HyperwalletAPICheckStatus.DOWN)
				.error("ERROR")
				.location("LOCATION")
				.build();
		//@formatter:on
		final Health result = testObj.from(hyperwalletAPICheck);

		assertThat(result.getStatus()).isEqualTo(Status.DOWN);
		assertThat(result.getDetails()).containsEntry("error", "ERROR");
		assertThat(result.getDetails()).containsEntry("location", "LOCATION");
	}

}
