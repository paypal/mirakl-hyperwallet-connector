package com.paypal.notifications.controllers;

import com.hyperwallet.clientsdk.model.HyperwalletWebhookNotification;
import com.paypal.infrastructure.BusinessStakeholderTestHelper;
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
import java.util.Objects;

/**
 * Rest controller for handling incoming notifications from Hyperwallet
 */
@Slf4j
@RestController
@RequestMapping(value = "/webhooks")
@Profile({ "!prod" })
public class IncomingHyperwalletNotificationWebhookMockController {

	@Resource
	private NotificationService notificationService;

	@Resource
	private BusinessStakeholderTestHelper businessStakeholderTestHelper;

	@Resource
	private NotificationEntityService notificationEntityService;

	@Resource
	private Converter<HyperwalletWebhookNotification, NotificationEntity> notificationConverter;

	@Resource
	private NotificationEntityEvaluator notificationEntityEvaluator;

	@PostMapping("/notifications")
	@ResponseStatus(HttpStatus.OK)
	public void receiveIncomingNotification(@RequestBody final HyperwalletWebhookNotification incomingNotificationDTO,
			@RequestParam(name = "enable_stk", required = false) final String bstkTokenList,
			@RequestParam(name = "client_user_id", required = false) final String clientUserId) {

		log.info("Incoming params for stk: {'enable_stk': [{}], 'client_user_id': [{}]}", bstkTokenList, clientUserId);

		if (Objects.nonNull(bstkTokenList) && Objects.nonNull(clientUserId)) {

			businessStakeholderTestHelper.storeRequiredVerificationBstk(bstkTokenList, clientUserId);
		}

		final NotificationEntity notificationEntity = notificationConverter.convert(incomingNotificationDTO);

		notificationEntityService.saveNotification(notificationEntity);

		if (notificationEntityEvaluator.isProcessable(notificationEntity)) {

			notificationService.processNotification(incomingNotificationDTO);
		}
	}

}
