package com.paypal.notifications.dto.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.notifications.dto.NotificationInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationInfoEntityToNotificationInfoDTOConverter
		implements Converter<NotificationInfoEntity, NotificationInfoDTO> {

	@Override
	public NotificationInfoDTO convert(final NotificationInfoEntity source) {
		NotificationInfoDTO target = new NotificationInfoDTO();
		target.setNotificationToken(source.getNotificationToken());
		target.setCreationDate(source.getCreationDate());
		target.setProgram(source.getProgram());
		target.setRetryCounter(source.getRetryCounter());
		target.setType(source.getType());
		target.setTarget(source.getTarget());

		return target;
	}

}
