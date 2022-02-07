package com.paypal.notifications.repository;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link NotificationsRepository} that tries to retrieve notifications
 * first for an internal memory storage and if not found connects to Hyperwallet API to
 * retrieve the notifications.
 *
 * It's main purpose is to help the testing process allowing to mock the responses from
 * Hyperwallet instead of directly connecting to it.
 */
@Component
@Profile({ "!prod" })
@Primary
public class NotificationsRepositoryMockImpl implements NotificationsRepository {

	private NotificationsRepositoryImpl hyperwalletNotificationsRepository;

	private NotificationsInMemoryStore notificationsStore;

	public NotificationsRepositoryMockImpl(final NotificationsRepositoryImpl hyperwalletNotificationsRepository,
			final NotificationsInMemoryStore notificationsStore) {
		this.hyperwalletNotificationsRepository = hyperwalletNotificationsRepository;
		this.notificationsStore = notificationsStore;
	}

	@Override
	public HyperwalletWebhookNotification getHyperwalletWebhookNotification(final String program, final String token) {
		return notificationsStore.getNotificationByToken(token)
				.orElseGet(() -> hyperwalletNotificationsRepository.getHyperwalletWebhookNotification(program, token));
	}

}
