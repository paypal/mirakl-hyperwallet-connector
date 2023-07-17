package com.paypal.notifications.incoming.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.notifications.incoming.services.NotificationProcessingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller for handling incoming notifications from Hyperwallet
 */
@Slf4j
@RestController
@RequestMapping("/webhooks")
public class IncomingHyperwalletNotificationWebhookController {

	@Resource
	private NotificationProcessingService notificationProcessingService;

	@PostMapping("/notifications")
	@ResponseStatus(HttpStatus.OK)
	public void receiveIncomingNotification(@RequestBody final HyperwalletWebhookNotification incomingNotificationDTO) {
		notificationProcessingService.processNotification(incomingNotificationDTO);
	}

}
