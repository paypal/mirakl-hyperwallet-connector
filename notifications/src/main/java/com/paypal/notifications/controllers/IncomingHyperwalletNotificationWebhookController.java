package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.notifications.evaluator.NotificationEntityEvaluator;
import com.paypal.notifications.model.entity.NotificationEntity;
import com.paypal.notifications.service.NotificationService;
import com.paypal.notifications.service.hmc.NotificationEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Rest controller for handling incoming notifications from Hyperwallet
 */
@Slf4j
@RestController
@RequestMapping("/webhooks")
@Profile({ "prod" })
public class IncomingHyperwalletNotificationWebhookController {

	@Resource
	private NotificationService notificationService;

	@Resource
	private NotificationEntityService notificationEntityService;

	@Resource
	private NotificationEntityEvaluator notificationEntityEvaluator;

	@Resource
	private Converter<HyperwalletWebhookNotification, NotificationEntity> notificationConverter;

	@PostMapping("/notifications")
	@ResponseStatus(HttpStatus.OK)
	public void receiveIncomingNotification(@RequestBody final HyperwalletWebhookNotification incomingNotificationDTO) {

		final NotificationEntity notificationEntity = notificationConverter.convert(incomingNotificationDTO);

		notificationEntityService.saveNotification(notificationEntity);

		if (notificationEntityEvaluator.isProcessable(notificationEntity)) {

			notificationService.processNotification(incomingNotificationDTO);
		}
	}

}
