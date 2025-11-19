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

}
