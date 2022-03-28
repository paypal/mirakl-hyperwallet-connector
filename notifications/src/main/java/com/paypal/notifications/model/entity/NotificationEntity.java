package com.paypal.notifications.model.entity;

import com.paypal.notifications.model.notification.NotificationType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
