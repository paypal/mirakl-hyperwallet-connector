package com.paypal.notifications.storage.repositories.entities;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity class for Notifications.
 */
@Entity
@Data
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
