package com.paypal.notifications.storage.repositories.entities;

/**
 * Notification type of entity {@link NotificationEntity}.
 */
public enum NotificationType {

	//@formatter:off
	/**
	 * Each type corresponds with the following Mirakl types:
	 * USR -> Seller
	 * PMT -> Payment
	 * STK -> Stakeholder
	 * TRM -> Bank account
	 * UNK -> Unknown
	 */
	//@formatter:off

	USR, PMT, STK, TRM, UNK

}
