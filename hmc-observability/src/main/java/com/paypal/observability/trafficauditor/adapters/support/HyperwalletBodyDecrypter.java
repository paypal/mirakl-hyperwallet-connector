package com.paypal.observability.trafficauditor.adapters.support;

import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HyperwalletBodyDecrypter {

	private final HyperwalletEncryption hyperwalletEncryption;

	public HyperwalletBodyDecrypter(final @Nullable HyperwalletEncryption hyperwalletEncryption) {
		this.hyperwalletEncryption = hyperwalletEncryption;
	}

	public String decryptBodyIfNeeded(final String body) {
		if (body == null || body.isBlank() || hyperwalletEncryption == null) {
			return body;
		}
		try {
			return hyperwalletEncryption.decrypt(body);
		}
		catch (final Exception e) {
			log.trace("Failed to decrypt body during Hyperwallet HTTP traffic capture", e);
			return body;
		}
	}

}
