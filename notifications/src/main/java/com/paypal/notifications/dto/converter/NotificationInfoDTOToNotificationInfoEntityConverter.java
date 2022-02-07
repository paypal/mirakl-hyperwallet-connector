package com.paypal.notifications.dto.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.notifications.dto.NotificationInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationInfoDTOToNotificationInfoEntityConverter
		implements Converter<NotificationInfoDTO, NotificationInfoEntity> {

	@Override
	public NotificationInfoEntity convert(final NotificationInfoDTO source) {
		NotificationInfoEntity target = new NotificationInfoEntity();
		target.setNotificationToken(source.getNotificationToken());
		target.setCreationDate(source.getCreationDate());
		target.setProgram(source.getProgram());
		target.setRetryCounter(source.getRetryCounter());
		target.setType(source.getType());
		target.setTarget(source.getTarget());

		return target;
	}

}
