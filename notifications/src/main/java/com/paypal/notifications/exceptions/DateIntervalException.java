package com.paypal.notifications.exceptions;

import com.paypal.infrastructure.exceptions.HMCException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Exception for handling date interval errors, it is when from date is after to date.
 */
public class DateIntervalException extends HMCException {

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String DATE_INTERVAL_ERROR_MESSAGE = "[From] date [%s] can not be later than [To] date [%s]";

	/**
	 * from {@link Date}.
	 */
	private final Date from;

	/**
	 * to {@link Date}.
	 */
	private final Date to;

	public DateIntervalException(final Date from, final Date to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Overrides the error message for using an specific error message format.
	 * @return an error message with the following format: [From] date {@code from} can
	 * not be later than [To] date {@code to}.
	 */
	@Override
	public String getMessage() {

		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

		return String.format(DATE_INTERVAL_ERROR_MESSAGE, dateFormat.format(this.from), dateFormat.format(this.to));
	}

}
