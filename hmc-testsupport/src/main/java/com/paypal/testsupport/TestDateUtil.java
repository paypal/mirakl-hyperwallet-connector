package com.paypal.testsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TestDateUtil {

	private TestDateUtil() {
	}

	public static Date from(final String date) {
		return from("yyyy-MM-dd", date);
	}

	public static Date from(final String format, final String date) {
		try {
			return new SimpleDateFormat(format).parse(date);
		}
		catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static boolean withinInterval(final Date date, final int seconds) {
		final Date intervalStart = new Date(System.currentTimeMillis() - seconds * 1000);
		final Date intervalEnd = new Date(System.currentTimeMillis() + seconds * 1000);

		return date.after(intervalStart) && date.before(intervalEnd);
	}

	public static Date currentDateMinusDays(final long days) {
		return new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000);
	}

	public static Date currentDateMinusDaysPlusSeconds(final long days, final long seconds) {
		return new Date(System.currentTimeMillis() + (seconds * 1000) - (days * 24 * 60 * 60 * 1000));
	}

}
