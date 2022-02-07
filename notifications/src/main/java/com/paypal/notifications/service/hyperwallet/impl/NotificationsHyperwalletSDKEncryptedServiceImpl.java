package com.paypal.notifications.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.util.HyperwalletEncryption;
import com.paypal.notifications.infrastructure.configuration.NotificationsHyperwalletApiConfig;
import com.paypal.notifications.service.hyperwallet.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "encrypted", "qaEncrypted" })
public class NotificationsHyperwalletSDKEncryptedServiceImpl extends NotificationsHyperwalletSDKServiceImpl {

	private final HyperwalletEncryption hyperwalletEncryption;

	public NotificationsHyperwalletSDKEncryptedServiceImpl(final NotificationsHyperwalletApiConfig notification,
			final HyperwalletEncryption hyperwalletEncryption) {
		super(notification);
		this.hyperwalletEncryption = hyperwalletEncryption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstance(final String issuingStore) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(notificationHyperwalletApiConfig.getUserStoreTokens())
				.map(tokens -> tokens.get(issuingStore))
				.orElse(null);
		//@formatter:on
		return new Hyperwallet(notificationHyperwalletApiConfig.getUsername(),
				notificationHyperwalletApiConfig.getPassword(), programUserToken,
				notificationHyperwalletApiConfig.getServer(), hyperwalletEncryption);
	}

}
