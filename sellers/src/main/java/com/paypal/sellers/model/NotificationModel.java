package com.paypal.sellers.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class NotificationModel<T extends NotificationBodyModel> {

	private final String token;

	private final String eventType;

	private final String createdOn;

	private final T object;

}
