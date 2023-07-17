package com.paypal.infrastructure.support.date;

import com.paypal.infrastructure.support.date.TimeMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TimeMachineTest {

	@Test
	void now_shouldReturnLocalDateTimeBasedOnFixedTime() {
		final LocalDateTime fixedDate = LocalDateTime.of(2000, 11, 5, 12, 30, 22);
		TimeMachine.useFixedClockAt(fixedDate);

		final LocalDateTime result = TimeMachine.now();

		assertThat(result).isEqualTo(fixedDate);
	}

}
