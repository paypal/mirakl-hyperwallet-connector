package com.paypal.infrastructure.hyperwallet.constants;

/**
 * Holds HyperWallet constants
 */
public final class HyperWalletConstants {

	private HyperWalletConstants() {
	}

	public static final String HYPERWALLET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String PAYMENT_OPERATOR_SUFFIX = "-operatorFee";

	public static final String JWKSET_ERROR_FILENAME = "public_keys_error.json";

	// Mirakl's API allows returning a maximum of 100 results per "page" in any API call
	// with pagination support
	public static final int MIRAKL_MAX_RESULTS_PER_PAGE = 100;

}
