package com.paypal.notifications.exceptions;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

class DateIntervalExceptionTest {

	private static final String FROM_ATTRIBUTE = "from";

	private static final String TO_ATTRIBUTE = "to";

	private static final Date FROM_DATE = new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime();

	private static final Date TO_DATE = new GregorianCalendar(2015, Calendar.FEBRUARY, 11).getTime();

	private static final String DATE_INTERVAL_FORMATTED_MESSAGE = "[From] date [2015-02-11T00:00:00] can not be later than [To] date [2014-02-11T00:00:00]";

	@Test
	void constructor_ShouldPopulateFromAndToDate_WhenCalled() {

		final DateIntervalException result = new DateIntervalException(FROM_DATE, TO_DATE);

		assertThat(result).hasFieldOrPropertyWithValue(FROM_ATTRIBUTE, FROM_DATE)
				.hasFieldOrPropertyWithValue(TO_ATTRIBUTE, TO_DATE);
	}

	@Test
	void getMessage_ShouldReturnADateIntervalErrorMessage() {

		final DateIntervalException result = new DateIntervalException(TO_DATE, FROM_DATE);

		assertThat(result.getMessage()).isEqualTo(DATE_INTERVAL_FORMATTED_MESSAGE);
	}

}
