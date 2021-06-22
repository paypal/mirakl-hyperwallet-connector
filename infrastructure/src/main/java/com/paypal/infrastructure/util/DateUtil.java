package com.paypal.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Utility class to handle dates
 */
@Slf4j
public class DateUtil {

	public static final TimeZone TIME_UTC = TimeZone.getTimeZone("UTC");

	private DateUtil() {
	}

	/**
	 * Converts a {@link Date} into a {@link LocalDateTime}
	 * @param date the {@link Date} to convert
	 * @return the date converted into {@link LocalDateTime}
	 */
	public static LocalDateTime convertToLocalDateTime(final Date date) {
		final ZoneId defaultZoneId = ZoneId.systemDefault();
		final Instant instant = date.toInstant();
		return instant.atZone(defaultZoneId).toLocalDateTime();
	}

	/**
	 * Converts a {@link LocalDateTime} into a {@link Date}
	 * @param localDateTime the {@link LocalDateTime} to convert
	 * @param zoneId
	 * @return the date converted into {@link Date}
	 */
	public static Date convertToDate(final LocalDateTime localDateTime, final ZoneId zoneId) {
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}

	/**
	 * Converts a {@link LocalDate} into a {@link Date}
	 * @param localDate the {@link LocalDate} to convert
	 * @param zoneId
	 * @return the date converted into {@link Date}
	 */
	public static Date convertToDate(final LocalDate localDate, final ZoneId zoneId) {
		return Date.from(localDate.atStartOfDay(zoneId).toInstant());
	}

	/**
	 * Converts a {@link String} into a {@link Date}
	 * @param stringDate the {@link String} to convert
	 * @param timeZone timezone used to parse the date
	 * @return the date converted into {@link Date}
	 */
	public static Date convertToDate(final String stringDate, final String dateFormat, final TimeZone timeZone) {
		try {
			final DateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
			format.setTimeZone(timeZone);

			return format.parse(stringDate);
		}
		catch (final ParseException ex) {
			log.error("Invalid date format [{}] for date [{}]", dateFormat, stringDate);
			log.error(ex.getMessage(), ex);
			return null;
		}

	}

	/**
	 * Converts a {@link LocalDate} into a {@link String}
	 * @param localDateTime the {@link LocalDate} to convert
	 * @param dateFormatPattern date format required
	 * @return the date converted into {@link String}
	 */
	public static String convertToString(final LocalDateTime localDateTime, final String dateFormatPattern) {
		if (StringUtils.isNotEmpty(dateFormatPattern) && Objects.nonNull(localDateTime)) {
			final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateFormatPattern, Locale.ENGLISH);
			return localDateTime.format(dateFormat);
		}
		return StringUtils.EMPTY;

	}

}
