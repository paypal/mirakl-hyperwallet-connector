package com.paypal.notifications.management.controllers.converters;

import com.paypal.notifications.management.controllers.dto.FailedNotificationInfoDTO;
import com.paypal.notifications.storage.repositories.entities.NotificationInfoEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface FailedNotificationsDtoConverter {

	FailedNotificationInfoDTO from(NotificationInfoEntity source);

	NotificationInfoEntity from(FailedNotificationInfoDTO source);

	default Page<FailedNotificationInfoDTO> from(final Page<NotificationInfoEntity> source) {
		return source.map(this::from);
	}

}
