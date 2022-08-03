package com.paypal.notifications.repository;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.notifications.service.hyperwallet.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link NotificationsRepository} that connects to Hyperwallet API to
 * retrieve previously generated notifications.
 */
@Slf4j
@Component
public class NotificationsRepositoryImpl implements NotificationsRepository {

	private final HyperwalletSDKService hyperwalletSDKService;

	public NotificationsRepositoryImpl(final HyperwalletSDKService hyperwalletSDKService) {
		this.hyperwalletSDKService = hyperwalletSDKService;
	}

	@Override
	public HyperwalletWebhookNotification getHyperwalletWebhookNotification(final String program, final String token) {
		final Hyperwallet hyperwalletInstance = hyperwalletSDKService.getHyperwalletInstance(program);
		try {
			return hyperwalletInstance.getWebhookEvent(token);
		}
		catch (final HyperwalletException ex) {
			log.error(String.format("Could not fetch notification [%s] due to reason:%n%s", token,
					HyperwalletLoggingErrorsUtil.stringify(ex)), ex);
			return null;
		}
	}

}
