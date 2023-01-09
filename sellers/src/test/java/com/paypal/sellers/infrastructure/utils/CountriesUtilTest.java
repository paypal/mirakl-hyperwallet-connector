package com.paypal.sellers.infrastructure.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

class CountriesUtilTest {

	/**
	 * When isocode is null Then is returned an empty.
	 */
	@Test
	void whenIsocodeIsNull_ThenIsReturnedAnEmptyOptional() {
		final String isocode = null;
		Optional<Locale> result = CountriesUtil.getLocaleByIsocode(isocode);
		Assertions.assertTrue(result.isEmpty());
	}

	/**
	 * When isocode not exists Then is returned an empty.
	 */
	@Test
	void whenIsocodeNotExists_ThenIsReturnedAnEmptyOptional() {
		final String isocode = "not_exist";
		Optional<Locale> result = CountriesUtil.getLocaleByIsocode(isocode);
		Assertions.assertTrue(result.isEmpty());
	}

	/**
	 * When uppercase isocode with two characters exists Then is returned the correct
	 * locale
	 */
	@Test
	void whenIsocodeExists_ThenIsReturnedHisLocale() {
		final String isocode = "GB";
		final Locale expected = new Locale("", isocode);
		Optional<Locale> result = CountriesUtil.getLocaleByIsocode(isocode);
		Assertions.assertEquals(expected, result.get());
	}

	@ParameterizedTest
	@MethodSource
	void getLocaleByIsocode_shouldIgnoreCase(String isocode, Locale expectedLocale) {
		Optional<Locale> result = CountriesUtil.getLocaleByIsocode(isocode);
		Assertions.assertEquals(expectedLocale, result.get());
	}

	private static Stream<Arguments> getLocaleByIsocode_shouldIgnoreCase() {
		//@formatter:off
		Locale expectedLocale = new Locale("", "GB");
		return Stream.of(
				Arguments.of("Gbr", expectedLocale),
				Arguments.of("gbr", expectedLocale),
				Arguments.of("GBR", expectedLocale)
		);
		//@formatter:on
	}

}
