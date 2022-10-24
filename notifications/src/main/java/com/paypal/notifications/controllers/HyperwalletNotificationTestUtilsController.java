package com.paypal.notifications.controllers;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.model.entity.NotificationInfoEntity;
import com.paypal.infrastructure.repository.FailedNotificationInformationRepository;
import com.paypal.notifications.dto.NotificationInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

/**
 * Rest controller that adds utilities for notifications used in automated tests
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Profile({ "qa", "qaEncrypted", "k6" })
public class HyperwalletNotificationTestUtilsController {

	public static final String NOTIFICATION_NOT_FOUND = "Notification not found";

	private FailedNotificationInformationRepository failedNotificationInformationRepository;

	private Converter<NotificationInfoDTO, NotificationInfoEntity> notificationInfoDTOToNotificationInfoEntityConverter;

	private Converter<NotificationInfoEntity, NotificationInfoDTO> notificationInfoEntityToNotificationInfoDTOConverter;

	public HyperwalletNotificationTestUtilsController(
			final FailedNotificationInformationRepository failedNotificationInformationRepository,
			final Converter<NotificationInfoDTO, NotificationInfoEntity> notificationInfoDTOToNotificationInfoEntityConverter,
			final Converter<NotificationInfoEntity, NotificationInfoDTO> notificationInfoEntityToNotificationInfoDTOConverter) {
		this.failedNotificationInformationRepository = failedNotificationInformationRepository;
		this.notificationInfoDTOToNotificationInfoEntityConverter = notificationInfoDTOToNotificationInfoEntityConverter;
		this.notificationInfoEntityToNotificationInfoDTOConverter = notificationInfoEntityToNotificationInfoDTOConverter;
	}

	@GetMapping("/failed-notifications/{notificationToken}")
	public ResponseEntity<Object> checkNotificationExistsInDatabaseByToken(
			@PathVariable final String notificationToken) {
		final NotificationInfoEntity notification = failedNotificationInformationRepository
				.findByNotificationToken(notificationToken);
		if (notification == null) {
			return new ResponseEntity<>(NOTIFICATION_NOT_FOUND, NOT_FOUND);
		}

		return new ResponseEntity<>(notificationInfoEntityToNotificationInfoDTOConverter.convert(notification), OK);
	}

	@GetMapping("/failed-notification-types/{notificationType}/targets/{targetToken}")
	public ResponseEntity<Object> checkNotificationExistsInDatabaseByTypeAndTarget(
			@PathVariable final String notificationType, @PathVariable final String targetToken) {
		final NotificationInfoEntity notification = failedNotificationInformationRepository
				.findByTypeAndTarget(notificationType, targetToken);
		if (notification == null) {
			return new ResponseEntity<>(NOTIFICATION_NOT_FOUND, NOT_FOUND);
		}

		return new ResponseEntity<>(notificationInfoEntityToNotificationInfoDTOConverter.convert(notification), OK);
	}

	@PostMapping("/failed-notifications/")
	@ResponseStatus(OK)
	public void saveNotificationInDatabase(@RequestBody final NotificationInfoDTO notification) {
		try {
			failedNotificationInformationRepository
					.save(notificationInfoDTOToNotificationInfoEntityConverter.convert(notification));
		}
		catch (final Exception e) {
			log.error("The notification could not be stored");
			throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/failed-notifications/")
	@ResponseStatus(OK)
	public void replaceNotificationInDatabase(@RequestBody final List<NotificationInfoDTO> notifications) {
		try {
			failedNotificationInformationRepository.deleteAll();
			failedNotificationInformationRepository.saveAll(notifications.stream()
					.map(notificationInfoDTOToNotificationInfoEntityConverter::convert).collect(Collectors.toList()));
		}
		catch (final Exception e) {
			log.error("The notification could not be stored");
			throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
		}
	}

}
