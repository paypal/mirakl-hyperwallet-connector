package com.paypal.notifications.management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.notifications.management.controllers.dtos.FailedNotificationInfo;
import com.paypal.notifications.storage.repositories.NotificationEntityRepository;
import com.paypal.notifications.storage.repositories.entities.NotificationEntity;
import com.paypal.notifications.storage.repositories.entities.NotificationStatus;
import com.paypal.notifications.storage.repositories.entities.NotificationType;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for {@link FailedNotificationsController}.
 * <p>
 * Uses MockMvc against the real Spring context and an in-memory H2 database to exercise
 * the full HTTP cycle for each endpoint defined in {@code management-api.yaml}.
 */
@AutoConfigureMockMvc(addFilters = false)
class FailedNotificationsControllerITTest extends AbstractIntegrationTest {

	private static final String BASE_URL = "/management/failed-notifications/";

	private static final String WEBHOOK_TOKEN_1 = "wbh-it-failed-001";

	private static final String WEBHOOK_TOKEN_2 = "wbh-it-failed-002";

	private static final String OBJECT_TOKEN = "usr-it-obj-001";

	private static final String PROGRAM_TOKEN = "prg-it-prg-001";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private NotificationEntityRepository notificationEntityRepository;

	@AfterEach
	void cleanUp() {
		notificationEntityRepository.deleteAll();
	}

	// ── GET /management/failed-notifications/ ────────────────────────────────────

	@Test
	void find_withNoData_shouldReturnEmptyPagedCollection() throws Exception {
		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(0));
	}

	@Test
	void find_withFailedNotifications_shouldReturnThemInPage() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);
		saveFailedEntity(WEBHOOK_TOKEN_2, NotificationType.PMT, "pmt-it-obj-002");

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(2));
	}

	@Test
	void find_shouldNotReturnPendingSuccessOrOutdatedNotifications() throws Exception {
		saveEntityWithStatus(WEBHOOK_TOKEN_1, NotificationStatus.PENDING);
		saveEntityWithStatus(WEBHOOK_TOKEN_2, NotificationStatus.SUCCESS);
		saveEntityWithStatus("wbh-it-outdated-001", NotificationStatus.OUTDATED);
		saveFailedEntity("wbh-it-failed-003", NotificationType.USR, OBJECT_TOKEN);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(1));
	}

	@Test
	void find_shouldReturnRetryingNotificationsAlongsideFailed() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);
		saveEntityWithStatus(WEBHOOK_TOKEN_2, NotificationStatus.RETRYING);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(2));
	}

	@Test
	void find_withTypeFilter_shouldReturnOnlyMatchingType() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);
		saveFailedEntity(WEBHOOK_TOKEN_2, NotificationType.PMT, "pmt-it-obj-002");

		mockMvc.perform(get(BASE_URL).param("type", "USR")).andExpect(status().isOk())
				.andExpect(jsonPath("$.page.totalElements").value(1))
				.andExpect(jsonPath("$._embedded['failed-notifications'][0].notificationToken").value(WEBHOOK_TOKEN_1));
	}

	@Test
	void find_withTargetFilter_shouldReturnOnlyMatchingObjectToken() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);
		saveFailedEntity(WEBHOOK_TOKEN_2, NotificationType.USR, "usr-it-obj-999");

		mockMvc.perform(get(BASE_URL).param("target", OBJECT_TOKEN)).andExpect(status().isOk())
				.andExpect(jsonPath("$.page.totalElements").value(1))
				.andExpect(jsonPath("$._embedded['failed-notifications'][0].notificationToken").value(WEBHOOK_TOKEN_1));
	}

	// ── GET /management/failed-notifications/{token} ─────────────────────────────

	@Test
	void get_whenTokenExists_shouldReturnNotificationDto() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);

		mockMvc.perform(get(BASE_URL + WEBHOOK_TOKEN_1)).andExpect(status().isOk())
				.andExpect(jsonPath("$.notificationToken").value(WEBHOOK_TOKEN_1))
				.andExpect(jsonPath("$.type").value("USR")).andExpect(jsonPath("$.target").value(OBJECT_TOKEN));
	}

	@Test
	void get_whenTokenNotFound_shouldReturn404() throws Exception {
		mockMvc.perform(get(BASE_URL + "wbh-nonexistent")).andExpect(status().isNotFound());
	}

	// ── POST /management/failed-notifications/ ───────────────────────────────────

	@Test
	void add_shouldPersistNewFailedNotificationAndReturn201() throws Exception {
		final FailedNotificationInfo info = buildDto(WEBHOOK_TOKEN_1, "USR", OBJECT_TOKEN);

		mockMvc.perform(
				post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(info)))
				.andExpect(status().isCreated());

		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_1)).isPresent().get()
				.extracting(NotificationEntity::getStatus).isEqualTo(NotificationStatus.FAILED);
	}

	// ── PUT /management/failed-notifications/{token} ─────────────────────────────

	@Test
	void update_whenTokenExists_shouldUpdateFieldsAndReturn200() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);

		final FailedNotificationInfo update = buildDto(WEBHOOK_TOKEN_1, "PMT", "pmt-updated-obj");
		update.setProgram("prg-updated");
		update.setRetryCounter(3);

		mockMvc.perform(put(BASE_URL + WEBHOOK_TOKEN_1).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(update))).andExpect(status().isOk())
				.andExpect(jsonPath("$.notificationToken").value(WEBHOOK_TOKEN_1))
				.andExpect(jsonPath("$.retryCounter").value(3));

		final NotificationEntity saved = notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_1).orElseThrow();
		assertThat(saved.getProgram()).isEqualTo("prg-updated");
		assertThat(saved.getRetryCounter()).isEqualTo(3);
	}

	@Test
	void update_whenTokenNotFound_shouldReturn404() throws Exception {
		final FailedNotificationInfo update = buildDto("wbh-nonexistent", "USR", OBJECT_TOKEN);

		mockMvc.perform(put(BASE_URL + "wbh-nonexistent").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(update))).andExpect(status().isNotFound());
	}

	// ── PUT /management/failed-notifications/ (replace) ──────────────────────────

	@Test
	void replace_shouldDeleteAllFailedAndPersistNewList() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);
		saveFailedEntity(WEBHOOK_TOKEN_2, NotificationType.PMT, "pmt-it-obj-002");

		final FailedNotificationInfo newEntry = buildDto("wbh-it-replaced-001", "USR", "usr-replaced-obj");

		mockMvc.perform(put(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(newEntry)))).andExpect(status().isOk());

		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_1)).isEmpty();
		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_2)).isEmpty();
		assertThat(notificationEntityRepository.findByWebHookToken("wbh-it-replaced-001")).isPresent();
	}

	@Test
	void replace_shouldAlsoDeleteRetryingNotificationsBeforePersistingNewList() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);
		saveEntityWithStatus(WEBHOOK_TOKEN_2, NotificationStatus.RETRYING);

		final FailedNotificationInfo newEntry = buildDto("wbh-it-replaced-002", "USR", "usr-replaced-obj");

		mockMvc.perform(put(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(List.of(newEntry)))).andExpect(status().isOk());

		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_1)).isEmpty();
		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_2)).isEmpty();
		assertThat(notificationEntityRepository.findByWebHookToken("wbh-it-replaced-002")).isPresent();
	}

	// ── DELETE /management/failed-notifications/{token} ──────────────────────────

	@Test
	void delete_whenTokenExists_shouldDeleteAndReturn200() throws Exception {
		saveFailedEntity(WEBHOOK_TOKEN_1, NotificationType.USR, OBJECT_TOKEN);

		mockMvc.perform(delete(BASE_URL + WEBHOOK_TOKEN_1)).andExpect(status().isOk());

		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_1)).isEmpty();
	}

	@Test
	void delete_whenTokenBelongsToRetryingNotification_shouldDeleteAndReturn200() throws Exception {
		saveEntityWithStatus(WEBHOOK_TOKEN_1, NotificationStatus.RETRYING);

		mockMvc.perform(delete(BASE_URL + WEBHOOK_TOKEN_1)).andExpect(status().isOk());

		assertThat(notificationEntityRepository.findByWebHookToken(WEBHOOK_TOKEN_1)).isEmpty();
	}

	@Test
	void delete_whenTokenNotFound_shouldReturn404() throws Exception {
		mockMvc.perform(delete(BASE_URL + "wbh-nonexistent")).andExpect(status().isNotFound());
	}

	// ── helpers ──────────────────────────────────────────────────────────────────

	private void saveFailedEntity(final String token, final NotificationType type, final String objectToken) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(token);
		entity.setObjectToken(objectToken);
		entity.setProgram(PROGRAM_TOKEN);
		entity.setNotificationType(type);
		entity.setStatus(NotificationStatus.FAILED);
		entity.setCreationDate(new Date());
		entity.setReceptionDate(new Date());
		notificationEntityRepository.save(entity);
	}

	private void saveEntityWithStatus(final String token, final NotificationStatus status) {
		final NotificationEntity entity = new NotificationEntity();
		entity.setWebHookToken(token);
		entity.setObjectToken(OBJECT_TOKEN);
		entity.setProgram(PROGRAM_TOKEN);
		entity.setNotificationType(NotificationType.USR);
		entity.setStatus(status);
		entity.setCreationDate(new Date());
		entity.setReceptionDate(new Date());
		notificationEntityRepository.save(entity);
	}

	private FailedNotificationInfo buildDto(final String token, final String type, final String target) {
		final FailedNotificationInfo dto = new FailedNotificationInfo();
		dto.setNotificationToken(token);
		dto.setType(type);
		dto.setTarget(target);
		dto.setProgram(PROGRAM_TOKEN);
		dto.setCreationDate(new Date());
		return dto;
	}

}
