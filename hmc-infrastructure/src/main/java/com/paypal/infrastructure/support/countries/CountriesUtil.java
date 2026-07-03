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
			final Locale locale = Locale.of("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(), locale);
			localeMap.put(country.toUpperCase(), locale);
		}
	}

	private CountriesUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Returns the {@link Locale} representation of a three letter isoCode country
	 * @param isoCode the isoCode representation
	 * @return the {@link Locale}
	 */
	public static Optional<Locale> getLocaleByIsoCode(final String isoCode) {
		if (StringUtils.isBlank(isoCode)) {
			return Optional.empty();
		}

		return Optional.ofNullable(localeMap.get(isoCode.toUpperCase()));
	}

}
