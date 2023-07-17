package com.paypal.notifications.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.notifications.failures.repositories.FailedNotificationInformationRepository;
import com.paypal.notifications.management.controllers.dto.FailedNotificationInfoDTO;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@formatter:off
@AutoConfigureMockMvc(addFilters = false)
class FailedNotificationsManagementITTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FailedNotificationInformationRepository failedNotificationInformationRepository;

	@Test
	void shouldSetFailedNotificationList() throws Exception {
		final List<FailedNotificationInfoDTO> failedNotificationInfoDTOs = List.of(
				FailedNotificationInfoMother.list("type-A", 10),
				FailedNotificationInfoMother.list("type-B", 10, 10))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(failedNotificationInfoDTOs)))
				.andDo(print())
				.andExpect(status().isOk());

		assertThat(failedNotificationInformationRepository.findAll()).hasSize(20);
	}

	@Test
	void shouldReplaceFailedNotificationList() throws Exception {
		List<FailedNotificationInfoDTO> failedNotificationInfoDTOs = List.of(
						FailedNotificationInfoMother.list("type-A", 10),
						FailedNotificationInfoMother.list("type-B", 10, 10))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(failedNotificationInfoDTOs)));

		failedNotificationInfoDTOs = List.of(
						FailedNotificationInfoMother.list("type-C", 8, 100),
						FailedNotificationInfoMother.list("type-D", 8, 200))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(failedNotificationInfoDTOs)))
				.andDo(print())
				.andExpect(status().isOk());

		assertThat(failedNotificationInformationRepository.findAll()).hasSize(16);
		assertThat(failedNotificationInformationRepository.findAll().stream()
				.filter(x -> x.getType().equals("type-C")))
				.hasSize(8);
		assertThat(failedNotificationInformationRepository.findAll().stream()
				.filter(x -> x.getType().equals("type-D")))
				.hasSize(8);
	}

	@Test
	void shouldGetNotificationById() throws Exception {
		final List<FailedNotificationInfoDTO> failedNotificationInfoDTOs = List.of(
						FailedNotificationInfoMother.list("type-A", 10),
						FailedNotificationInfoMother.list("type-B", 10, 10))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(failedNotificationInfoDTOs)));

		this.mockMvc.perform(get("/management/failed-notifications/wbh-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.notificationToken").value("wbh-1"));
	}

	@Test
	void shouldGetNotificationById_andReturnNotFound_whenNotificationNotExists() throws Exception {
		this.mockMvc.perform(put("/management/failed-notifications/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(List.of())));
		this.mockMvc.perform(get("/management/failed-notifications/wbh-1"))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldModifyNotification() throws Exception {
		final List<FailedNotificationInfoDTO> failedNotificationInfoDTOs = List.of(
						FailedNotificationInfoMother.list("type-A", 10),
						FailedNotificationInfoMother.list("type-B", 10, 10))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(failedNotificationInfoDTOs)));

		final FailedNotificationInfoDTO failedNotificationInfoDTO = FailedNotificationInfoMother.single("type-A", 1);
		failedNotificationInfoDTO.setRetryCounter(1);
		this.mockMvc.perform(put("/management/failed-notifications/wbh-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(failedNotificationInfoDTO)));

		this.mockMvc.perform(get("/management/failed-notifications/wbh-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.notificationToken").value("wbh-1"))
				.andExpect(jsonPath("$.retryCounter").value("1"));
	}

	@Test
	void shouldRemoveNotification() throws Exception {
		final List<FailedNotificationInfoDTO> failedNotificationInfoDTOs = List.of(
						FailedNotificationInfoMother.list("type-A", 10),
						FailedNotificationInfoMother.list("type-B", 10, 10))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(failedNotificationInfoDTOs)));

		final FailedNotificationInfoDTO failedNotificationInfoDTO = FailedNotificationInfoMother.single("type-A", 1);
		failedNotificationInfoDTO.setRetryCounter(1);
		this.mockMvc.perform(delete("/management/failed-notifications/wbh-1"));

		this.mockMvc.perform(get("/management/failed-notifications/wbh-1"))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldFindNotificationByTargetToken() throws Exception {
		final List<FailedNotificationInfoDTO> failedNotificationInfoDTOs = List.of(
						FailedNotificationInfoMother.list("type-A", 10),
						FailedNotificationInfoMother.list("type-B", 10, 10))
				.stream().flatMap(List::stream).toList();

		this.mockMvc.perform(put("/management/failed-notifications/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(failedNotificationInfoDTOs)));

		this.mockMvc.perform(get("/management/failed-notifications/?target=trg-1&type=type-A"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page.totalElements").value(1))
				.andExpect(jsonPath("$._embedded.failed-notifications[0].notificationToken").value("wbh-1"));
	}

	private String toJson(final Object obj) throws Exception {
		final ObjectMapper objectMapper = new ObjectMapper();
		final StringWriter stringWriter = new StringWriter();
		objectMapper.writeValue(stringWriter, obj);

		return stringWriter.toString();
	}

	static class FailedNotificationInfoMother {
		static FailedNotificationInfoDTO single(final String type, final int idx) {
			return FailedNotificationInfoDTO.builder()
					.notificationToken("wbh-%s".formatted(idx))
					.type(type)
					.target("trg-%s".formatted(idx))
					.program("prg-%s".formatted(0))
					.retryCounter(0)
					.creationDate(new Date())
					.build();
		}

		static List<FailedNotificationInfoDTO> list(final String type, final int num) {
			return list(type, num, 0);
		}

		static List<FailedNotificationInfoDTO> list(final String type, final int num, final int offset) {
			return IntStream
					.range(0, num)
					.mapToObj(idx -> FailedNotificationInfoMother.single(type, offset + idx))
					.collect(Collectors.toList());
		}

	}
}
