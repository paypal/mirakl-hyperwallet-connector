package com.paypal.notifications.storage.repositories.entities;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Class to hold information about failed notifications
 */
@Entity
@Data
public class NotificationInfoEntity implements Serializable {

	@Id
	protected String notificationToken;

	protected String type;

	protected String target;

	protected String program;

	protected int retryCounter;

	protected Date creationDate;

}
