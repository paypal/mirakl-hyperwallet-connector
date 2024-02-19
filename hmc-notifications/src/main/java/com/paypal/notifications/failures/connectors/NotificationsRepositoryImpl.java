package com.paypal.notifications.failures.connectors;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link NotificationsRepository} that connects to Hyperwallet API to
 * retrieve previously generated notifications.
 */
@Slf4j
@Component
public class NotificationsRepositoryImpl implements NotificationsRepository {

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	public NotificationsRepositoryImpl(final UserHyperwalletSDKService userHyperwalletSDKService) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
	}

	@Override
	public HyperwalletWebhookNotification getHyperwalletWebhookNotification(final String programToken,
			final String token) {
		final Hyperwallet hyperwalletInstance = userHyperwalletSDKService
				.getHyperwalletInstanceByProgramToken(programToken);
		try {
			return hyperwalletInstance.getWebhookEvent(token);
		}
		catch (final HyperwalletException ex) {
			log.error("Could not fetch notification [%s] due to reason:%n%s".formatted(token,
					HyperwalletLoggingErrorsUtil.stringify(ex)), ex);
			return null;
		}
	}

}
