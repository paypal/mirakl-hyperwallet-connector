package com.paypal.infrastructure.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Time machine class to help with date testing
 */
public class TimeMachine {

	private static Clock clock = Clock.systemDefaultZone();

	private static final ZoneId zoneId = ZoneId.systemDefault();

	private TimeMachine() {
		// doNothing
	}

	/**
	 * Returns the current {@link LocalDateTime}
	 * @return a {@link LocalDateTime}
	 */
	public static LocalDateTime now() {
		return LocalDateTime.now(getClock());
	}

	/**
	 * Fixes the clock date to an specific {@link LocalDateTime}
	 * @param date The {@link LocalDateTime}
	 */
	public static void useFixedClockAt(final LocalDateTime date) {
		clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
	}

	/**
	 * Sets the default zone to the system default one
	 */
	public static void useSystemDefaultZoneClock() {
		clock = Clock.systemDefaultZone();
	}

	/**
	 * Returns the {@link Clock}
	 * @return The {@link Clock}
	 */
	private static Clock getClock() {
		return clock;
	}

}
