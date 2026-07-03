package com.paypal.infrastructure.support.countries;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class CountriesUtilTest {

	/**
	 * When isoCode is null Then is returned an empty.
	 */
	@Test
	void whenIsoCodeIsNull_ThenIsReturnedAnEmptyOptional() {
		final String isoCode = null;
		final Optional<Locale> result = CountriesUtil.getLocaleByIsoCode(isoCode);
		Assertions.assertTrue(result.isEmpty());
	}

	/**
	 * When isoCode not exists Then is returned an empty.
	 */
	@Test
	void whenIsoCodeNotExists_ThenIsReturnedAnEmptyOptional() {
		final String isoCode = "not_exist";
		final Optional<Locale> result = CountriesUtil.getLocaleByIsoCode(isoCode);
		Assertions.assertTrue(result.isEmpty());
	}

	/**
	 * When uppercase isoCode with two characters exists Then is returned the correct
	 * locale
	 */
	@Test
	void whenIsoCodeExists_ThenIsReturnedHisLocale() {
		final String isoCode = "GB";
		final Locale expected = Locale.of("", isoCode);
		final Optional<Locale> result = CountriesUtil.getLocaleByIsoCode(isoCode);
		Assertions.assertEquals(expected, result.orElse(null));
	}

	@ParameterizedTest
	@MethodSource
	void getLocaleByIsoCode_shouldIgnoreCase(final String isoCode, final Locale expectedLocale) {
		final Optional<Locale> result = CountriesUtil.getLocaleByIsoCode(isoCode);
		Assertions.assertEquals(expectedLocale, result.orElse(null));
	}

	private static Stream<Arguments> getLocaleByIsoCode_shouldIgnoreCase() {
		//@formatter:off
		final Locale expectedLocale = Locale.of("", "GB");
		return Stream.of(
				Arguments.of("Gbr", expectedLocale),
				Arguments.of("gbr", expectedLocale),
				Arguments.of("GBR", expectedLocale)
		);
		//@formatter:on
	}

}
