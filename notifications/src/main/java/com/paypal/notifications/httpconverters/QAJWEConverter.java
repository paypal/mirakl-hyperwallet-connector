package com.paypal.notifications.httpconverters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Converts a JWE payload into the specific {@link HyperwalletWebhookNotification}
 */
@Component
@Profile({ "qaEncrypted" })
public class QAJWEConverter extends JWEConverter {

	public QAJWEConverter(
			final @Qualifier("hyperwalletQAEncryptionWrapper") HyperwalletEncryption hyperwalletEncryption,
			final ObjectMapper objectMapper) {
		super(hyperwalletEncryption, objectMapper);
	}

}
