package com.paypal.notifications.incoming.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache configuration for caching full {@code HyperwalletWebhookNotification}
 * objects by their webhook token. This avoids repeated calls to the Hyperwallet SDK when
 * the processing job retries a notification.
 */
@Configuration
public class NotificationCacheConfiguration {

	/**
	 * Bean name for the notification webhook cache manager.
	 */
	public static final String NOTIFICATION_CACHE_MANAGER = "notificationCacheManager";

	/**
	 * Cache name used to store {@code HyperwalletWebhookNotification} objects keyed by
	 * webhook token.
	 */
	public static final String WEBHOOK_NOTIFICATION_CACHE = "webhookNotificationCache";

	@Bean(NOTIFICATION_CACHE_MANAGER)
	public CacheManager notificationCacheManager(@Value("${hmc.webhooks.cache.max-size:5000}") final long maxSize,
			@Value("${hmc.webhooks.cache.expire-after-days:15}") final long expireAfterDays) {
		final Caffeine<Object, Object> caffeine = Caffeine.newBuilder().maximumSize(maxSize)
				.expireAfterWrite(expireAfterDays, TimeUnit.DAYS);
		final CaffeineCacheManager manager = new CaffeineCacheManager(WEBHOOK_NOTIFICATION_CACHE);
		manager.setCaffeine(caffeine);
		return manager;
	}

}
