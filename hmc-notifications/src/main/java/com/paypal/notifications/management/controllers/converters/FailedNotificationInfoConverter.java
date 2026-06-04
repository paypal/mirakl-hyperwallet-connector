package com.paypal.notifications.management.controllers.converters;

import com.paypal.notifications.management.controllers.dtos.FailedNotificationInfo;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

/**
 * MapStruct converter from {@link NotificationEntity} to {@link FailedNotificationInfo}.
 */
@Mapper(componentModel = "spring")
public interface FailedNotificationInfoConverter {

	/**
	 * Converts a single {@link NotificationEntity} to a {@link FailedNotificationInfo}.
	 * @param source the entity to convert.
	 * @return the resulting DTO.
	 */
	@Mapping(source = "webHookToken", target = "notificationToken")
	@Mapping(source = "notificationType", target = "type")
	@Mapping(source = "objectToken", target = "target")
	FailedNotificationInfo from(NotificationEntity source);

	/**
	 * Converts a {@link Page} of {@link NotificationEntity} to a {@link Page} of
	 * {@link FailedNotificationInfo}.
	 * @param source the page of entities to convert.
	 * @return a page of DTOs.
	 */
	default Page<FailedNotificationInfo> from(final Page<NotificationEntity> source) {
		return source.map(this::from);
	}

	/**
	 * Converts a {@link NotificationType} enum value to its string name, or {@code null}
	 * if the type is {@code null}.
	 * @param value the notification type enum value.
	 * @return the enum name as a string, or {@code null}.
	 */
	default String from(final NotificationType value) {
		return value == null ? null : value.name();
	}

}
