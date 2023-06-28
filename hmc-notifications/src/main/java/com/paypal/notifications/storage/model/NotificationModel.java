package com.paypal.notifications.storage.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Notification Model that holds all notification types coming from Hyperwallet
 *
 * @param <T> Class that must extend from NotificationModel
 */

@Getter
@Builder(toBuilder = true)
public class NotificationModel<T extends NotificationBodyModel> {

	private final String token;

	private final String eventType;

	private final String createdOn;

	private final T object;

}
