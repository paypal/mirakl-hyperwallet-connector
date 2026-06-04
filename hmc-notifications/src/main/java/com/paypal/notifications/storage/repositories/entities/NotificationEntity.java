package com.paypal.notifications.storage.repositories.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity class for Notifications.
 */
@Entity
@Data
@Table(name = "NOTIFICATION_ENTITY", indexes = { @Index(name = "wbh_token_idx", columnList = "webHookToken"),
		@Index(name = "obj_token_idx", columnList = "objectToken,creationDate") })
public class NotificationEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String webHookToken;

	private String objectToken;

	private Date creationDate;

	private Date receptionDate;

	private NotificationType notificationType;

	@Enumerated(EnumType.STRING)
	private NotificationStatus status = NotificationStatus.PENDING;

	private int retryCounter = 0;

	/**
	 * Timestamp of the last failed processing attempt. Populated each time the
	 * notification transitions to {@link NotificationStatus#RETRYING} or
	 * {@link NotificationStatus#FAILED}.
	 */
	private Date lastRetryDate;

	/**
	 * Earliest timestamp at which this notification is eligible to be retried. Computed
	 * using exponential back-off when the notification transitions to
	 * {@link NotificationStatus#RETRYING}: {@code lastRetryDate + initialDelay *
	 * multiplier^retryCounter}. {@code null} for {@code PENDING} notifications (always
	 * eligible) and {@code FAILED} notifications (never eligible again).
	 */
	private Date nextRetryDate;

	private String program;

}
