package com.paypal.infrastructure.support.countries;

import org.apache.commons.lang3.StringUtils;

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
		localeMap = new HashMap<>(countries.length * 2);
		for (final String country : countries) {
			final Locale locale = new Locale("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(), locale);
			localeMap.put(country.toUpperCase(), locale);
		}
	}

	private CountriesUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns the {@link Locale} representation of a three letter isocode country
	 * @param isocode the isocode representation
	 * @return the {@link Locale}
	 */
	public static Optional<Locale> getLocaleByIsocode(final String isocode) {
		if (StringUtils.isBlank(isocode)) {
			return Optional.empty();
		}

		return Optional.ofNullable(localeMap.get(isocode.toUpperCase()));
	}

}
