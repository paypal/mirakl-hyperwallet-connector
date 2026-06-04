package com.paypal.notifications.management.controllers.dtos;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;

/**
 * DTO representing a failed notification as described by the management API spec
 * {@code FailedNotificationInfo} schema.
 */
@Data
@Relation(collectionRelation = "failed-notifications")
public class FailedNotificationInfo {

	/**
	 * The webhook token that uniquely identifies the notification.
	 */
	private String notificationToken;

	/**
	 * * The short notification type code (e.g. {@code USR}, {@code PMT}, {@code STK}, *
	 * {@code TRM}). Maps to the
	 * {@link com.paypal.notifications.storage.repositories.entities.NotificationType} *
	 * enum name. Accepted values on write: {@code USR}, {@code PMT}, {@code STK}, *
	 * {@code TRM}, {@code UNK}.
	 */
	private String type;

	/**
	 * The object token of the subject entity (e.g. {@code usr-abc123}). Corresponds to
	 * {@code objectToken} on the entity.
	 */
	private String target;

	/**
	 * The Hyperwallet program token under which this notification was received.
	 */
	private String program;

	/**
	 * Number of processing attempts that have been made for this notification.
	 */
	private int retryCounter;

	/**
	 * The date/time at which the notification was originally received.
	 */
	private Date creationDate;

}
