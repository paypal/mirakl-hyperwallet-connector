package com.paypal.sellers.utils;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.paypal.sellers.utils.LanguageConversion.toIso6391;
import static org.assertj.core.api.Assertions.assertThat;

public class LanguageConversionTest {

	@Test
	public void convert_ShouldConvertLanguageTags() {
		assertThat(toIso6391(Locale.UK)).isEqualTo("en");
		assertThat(toIso6391(Locale.ENGLISH)).isEqualTo("en");
		assertThat(toIso6391(Locale.FRANCE)).isEqualTo("fr");
		assertThat(toIso6391(Locale.FRENCH)).isEqualTo("fr");
		assertThat(toIso6391(new Locale("es", "ES"))).isEqualTo("es");
		assertThat(toIso6391(null)).isEqualTo(null);
	}

}
