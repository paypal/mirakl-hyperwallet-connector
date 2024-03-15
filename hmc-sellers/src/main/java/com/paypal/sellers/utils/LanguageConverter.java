package com.paypal.sellers.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public final class LanguageConverter {

	private static final String CHINA_SIMPLIFIED = "zh_CN";

	@Value("#{'${hmc.hyperwallet.supported.languages}'.split(',')}")
	private List<String> supportedLanguages;

	@Value("#{'${hmc.hyperwallet.default.language}'}")
	private String defaultLanguage;

	protected LanguageConverter() {
	}

	/**
	 * Hyperwallet require that the language code be in ISO-639-1 format.
	 * @param input - the input string of locale (e.g. en_GB)
	 * @return the 2 digit language tag
	 * @see <a href=
	 * "https://docs.hyperwallet.com/content/references/v1/supported-languages">Supported
	 * Languages</a>
	 */
	public String convert(final Locale input) {
		return input != null ? getLanguage(input) : null;
	}

	private String getLanguage(final Locale input) {
		if (input.equals(Locale.CHINA)) {
			return CHINA_SIMPLIFIED;
		}
		else if (supportedLanguages.contains(input.getLanguage())) {
			return input.getLanguage();
		}
		else {
			return defaultLanguage;
		}
	}

}
