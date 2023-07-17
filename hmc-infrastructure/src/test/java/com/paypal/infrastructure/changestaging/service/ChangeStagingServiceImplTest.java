package com.paypal.infrastructure.changestaging.service;

import com.paypal.infrastructure.changestaging.model.Change;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import com.paypal.infrastructure.changestaging.repositories.StagedChangesRepository;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import com.paypal.infrastructure.changestaging.service.converters.StagedChangesEntityConverter;
import com.paypal.infrastructure.changestaging.service.converters.StagedChangesModelConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeStagingServiceImplTest {

	@InjectMocks
	@Spy
	private ChangeStagingServiceImpl testObj;

	@Mock
	private StagedChangesModelConverter stagedChangesModelConverterMock;

	@Mock
	private StagedChangesEntityConverter stagedChangesEntityConverterMock;

	@Mock
	private StagedChangesRepository stagedChangeRepositoryMock;

	@Test
	void stageChange_shouldCreateStageChange_andStoreIt() {
		// given
		final Change change = mock(Change.class);
		final StagedChange stagedChange = mock(StagedChange.class);
		final StagedChangeEntity stagedChangeEntity = mock(StagedChangeEntity.class);
		when(stagedChangesModelConverterMock.from(change)).thenReturn(stagedChange);
		when(stagedChangesEntityConverterMock.from(stagedChange)).thenReturn(stagedChangeEntity);

		// when
		final StagedChange result = testObj.stageChange(change);

		// then
		assertThat(result).isEqualTo(stagedChange);
		verify(stagedChangeRepositoryMock).save(stagedChangeEntity);
	}

	@Test
	void stageChanges_shouldInvokeStageChange_forEveryChange() {
		// given
		final Change change1 = mock(Change.class);
		final Change change2 = mock(Change.class);
		final StagedChange stagedChange1 = mock(StagedChange.class);
		final StagedChange stagedChange2 = mock(StagedChange.class);
		doReturn(stagedChange1).when(testObj).stageChange(change1);
		doReturn(stagedChange2).when(testObj).stageChange(change2);

		// when
		final List<StagedChange> result = testObj.stageChanges(List.of(change1, change2));

		// then
		assertThat(result).containsExactlyInAnyOrder(stagedChange1, stagedChange2);
	}

}
