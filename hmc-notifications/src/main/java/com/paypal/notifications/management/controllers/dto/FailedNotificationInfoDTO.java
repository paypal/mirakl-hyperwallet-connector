package com.paypal.notifications.management.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Relation(collectionRelation = "failed-notifications")
public class FailedNotificationInfoDTO {

	protected String notificationToken;

	protected String type;

	protected String target;

	protected String program;

	protected int retryCounter;

	protected Date creationDate;

}
