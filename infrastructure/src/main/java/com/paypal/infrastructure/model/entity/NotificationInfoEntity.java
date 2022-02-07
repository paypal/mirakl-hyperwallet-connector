package com.paypal.infrastructure.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
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
