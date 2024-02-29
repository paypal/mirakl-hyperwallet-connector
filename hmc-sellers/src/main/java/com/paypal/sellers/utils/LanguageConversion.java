package com.paypal.sellers.utils;

import java.util.Locale;

public final class LanguageConversion {

	private LanguageConversion() {
	}

	/**
	 * Hyperwallet require that the language code be in ISO-639-1 format.
	 * @param input - the input string of locale (e.g. en_GB)
	 * @return the 2 digit language tag
	 * @see <a href=
	 * "https://docs.hyperwallet.com/content/references/v1/supported-languages">Supported
	 * Languages</a>
	 */
	public static String toIso6391(final Locale input) {
		return input != null ? input.getLanguage() : null;
	}

}
