package com.paypal.infrastructure.changestaging;

import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import com.paypal.infrastructure.changestaging.repositories.StagedChangesRepository;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import com.paypal.infrastructure.changestaging.service.StagedChangesPoller;
import com.paypal.testsupport.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
class ChangeStagingManagementTest extends AbstractIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StagedChangesRepository stagedChangesRepository;

	@SpyBean
	private StagedChangesPoller stagedChangesPoller;

	@Test
	void shouldGetStagedChanges() throws Exception {
		stagedChangesRepository.saveAll(entities(10));

		this.mockMvc.perform(get("/management/staged-changes/")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.page.totalElements").value(10));
	}

	@Test
	void shouldProcessStagedChanges() throws Exception {
		stagedChangesRepository.saveAll(entities(10));

		this.mockMvc.perform(post("/management/staged-changes/process")).andDo(print()).andExpect(status().isOk());

		verify(stagedChangesPoller).performStagedChange();
	}

	private List<StagedChangeEntity> entities(final int num) {
		return IntStream.rangeClosed(1, num).mapToObj(this::entity).toList();
	}

	private StagedChangeEntity entity(final int idx) {
		final StagedChangeEntity stagedChangeEntity = new StagedChangeEntity();
		stagedChangeEntity.setId(String.valueOf(idx));
		stagedChangeEntity.setOperation(ChangeOperation.UPDATE);
		stagedChangeEntity.setType("type-" + idx);
		stagedChangeEntity.setTarget(ChangeTarget.MIRAKL);
		stagedChangeEntity.setPayload("payload-" + idx);
		stagedChangeEntity.setCreationDate(new Date());

		return stagedChangeEntity;
	}

}
