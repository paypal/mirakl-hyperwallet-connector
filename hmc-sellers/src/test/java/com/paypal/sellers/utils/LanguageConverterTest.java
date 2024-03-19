package com.paypal.sellers.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LanguageConverterTest {

	private static final String SUPPORTED_LANGUAGES = "supportedLanguages";

	private static final String DEFAULT_LANGUAGE = "defaultLanguage";

	@InjectMocks
	@Spy
	private LanguageConverter testObj;

	@BeforeEach
	void setUp() {
		final List<String> supportedLanguages = List.of("en", "fr", "es", "de", "it", "nl", "pt");
		ReflectionTestUtils.setField(testObj, DEFAULT_LANGUAGE, "en");
		ReflectionTestUtils.setField(testObj, SUPPORTED_LANGUAGES, supportedLanguages);
	}

	@Test
	void convert_shouldReturnENLanguageWhenLocaleIsUS() {

		final String result = testObj.convert(Locale.US);

		assertEquals("en", result);
	}

	@Test
	void convert_shouldReturnFRLanguageWhenLocaleIsFRANCE() {

		final String result = testObj.convert(Locale.FRANCE);

		assertEquals("fr", result);
	}

	@Test
	void convert_shouldReturnzh_CNLanguageWhenLocaleIsChinaSimplified() {

		final String result = testObj.convert(Locale.CHINA);

		assertEquals("zh_CN", result);
	}

	@Test
	void convert_shouldReturnENLanguageWhenLocaleIsNotSupported() {
		final Locale locale = new Locale("fi", "FI");

		final String result = testObj.convert(locale);

		assertEquals("en", result);
	}

}