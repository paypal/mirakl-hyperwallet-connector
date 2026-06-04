package com.paypal.notifications.incoming.cache;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Default implementation of {@link WebhookNotificationRetriever}. Uses a Caffeine-backed
 * {@link CacheManager} as the primary store and falls back to the Hyperwallet SDK on a
 * cache miss.
 */
@Slf4j
@Component
public class WebhookNotificationRetrieverImpl implements WebhookNotificationRetriever {

	private final CacheManager cacheManager;

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	public WebhookNotificationRetrieverImpl(
			@Qualifier(NotificationCacheConfiguration.NOTIFICATION_CACHE_MANAGER) final CacheManager cacheManager,
			final UserHyperwalletSDKService userHyperwalletSDKService) {
		this.cacheManager = cacheManager;
		this.userHyperwalletSDKService = userHyperwalletSDKService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletWebhookNotification get(final String programToken, final String webhookToken) {
		final Cache cache = getCache();
		final Cache.ValueWrapper cached = cache.get(webhookToken);
		if (cached != null) {
			log.debug("Cache hit for webhook notification [{}]", webhookToken);
			return (HyperwalletWebhookNotification) cached.get();
		}

		log.debug("Cache miss for webhook notification [{}] — fetching from Hyperwallet SDK", webhookToken);
		final HyperwalletWebhookNotification notification = fetchFromSdk(programToken, webhookToken);
		if (notification != null) {
			cache.put(webhookToken, notification);
		}
		return notification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(final HyperwalletWebhookNotification notification) {
		if (notification == null || notification.getToken() == null) {
			return;
		}
		getCache().put(notification.getToken(), notification);
		log.debug("Cached webhook notification [{}]", notification.getToken());
	}

	private HyperwalletWebhookNotification fetchFromSdk(final String programToken, final String webhookToken) {
		try {
			final Hyperwallet sdk = userHyperwalletSDKService.getHyperwalletInstanceByProgramToken(programToken);
			return sdk.getWebhookEvent(webhookToken);
		}
		catch (final HyperwalletException ex) {
			log.error("Could not fetch notification [%s] due to reason:%n%s".formatted(webhookToken,
					HyperwalletLoggingErrorsUtil.stringify(ex)), ex);
			return null;
		}
	}

	private Cache getCache() {
		return Optional.ofNullable(cacheManager.getCache(NotificationCacheConfiguration.WEBHOOK_NOTIFICATION_CACHE))
				.orElseThrow(() -> new IllegalStateException(
						"Cache [" + NotificationCacheConfiguration.WEBHOOK_NOTIFICATION_CACHE + "] is not configured"));
	}

}
