package com.paypal.notifications.repository;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link NotificationsRepository} that connects to Hyperwallet API to
 * retrieve previously generated notifications.
 */
@Slf4j
@Component
public class NotificationsRepositoryImpl implements NotificationsRepository {

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	public NotificationsRepositoryImpl(final HyperwalletSDKUserService hyperwalletSDKUserService) {
		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
	}

	@Override
	public HyperwalletWebhookNotification getHyperwalletWebhookNotification(final String program, final String token) {
		final Hyperwallet hyperwalletInstance = hyperwalletSDKUserService
				.getHyperwalletInstanceByHyperwalletProgram(program);
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
