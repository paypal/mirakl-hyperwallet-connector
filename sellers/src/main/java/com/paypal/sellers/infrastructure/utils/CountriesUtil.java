package com.paypal.sellers.infrastructure.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for helping with countries manipulation
 */
public final class CountriesUtil {

	private static final Map<String, Locale> localeMap;

	static {
		final String[] countries = Locale.getISOCountries();
		localeMap = new HashMap<>(countries.length);
		for (final String country : countries) {
			final Locale locale = new Locale("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(), locale);
		}
	}

	private CountriesUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns the {@link Locale} representation of a three letter isocode country
	 * @param isocode the 3-letter isocode representation
	 * @return the {@link Locale}
	 */
	public static Optional<Locale> getLocaleByThreeLettersIsocode(final String isocode) {
		return Optional.ofNullable(localeMap.get(isocode));
	}

}
