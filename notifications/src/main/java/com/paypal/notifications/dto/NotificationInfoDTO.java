package com.paypal.notifications.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationInfoDTO {

	protected String notificationToken;

	protected String type;

	protected String target;

	protected String program;

	protected int retryCounter;

	protected Date creationDate;

}
