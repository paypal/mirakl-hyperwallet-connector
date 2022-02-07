package com.paypal.notifications.service.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.paypal.notifications.infrastructure.configuration.NotificationsHyperwalletApiConfig;
import com.paypal.notifications.service.hyperwallet.HyperwalletSDKService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link HyperwalletSDKService}
 */
@Service
@Profile({ "!encrypted && !qaEncrypted" })
public class NotificationsHyperwalletSDKServiceImpl implements HyperwalletSDKService {

	protected final NotificationsHyperwalletApiConfig notificationHyperwalletApiConfig;

	public NotificationsHyperwalletSDKServiceImpl(
			final NotificationsHyperwalletApiConfig notificationHyperwalletApiConfig) {
		this.notificationHyperwalletApiConfig = notificationHyperwalletApiConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hyperwallet getHyperwalletInstance(final String hyperwalletProgram) {
		//@formatter:off
		final String programUserToken = Optional.ofNullable(notificationHyperwalletApiConfig.getUserStoreTokens())
				.map(tokens -> tokens.get(hyperwalletProgram))
				.orElse(null);
		//@formatter:on
		return new Hyperwallet(notificationHyperwalletApiConfig.getUsername(),
				notificationHyperwalletApiConfig.getPassword(), programUserToken,
				notificationHyperwalletApiConfig.getServer(), null);
	}

}
