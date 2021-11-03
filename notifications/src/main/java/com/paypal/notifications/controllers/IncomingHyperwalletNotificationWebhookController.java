package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.services.NotificationService;
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
@RequestMapping(value = "/webhooks")
@Profile({ "prod" })
public class IncomingHyperwalletNotificationWebhookController {

	@Resource
	private NotificationService notificationService;

	@PostMapping("/notifications")
	@ResponseStatus(HttpStatus.OK)
	public void receiveIncomingNotification(@RequestBody final HyperwalletWebhookNotification incomingNotificationDTO) {
		notificationService.processNotification(incomingNotificationDTO);
	}

}
