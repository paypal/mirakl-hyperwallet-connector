package com.paypal.infrastructure.changestaging.service.operations;

import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import com.paypal.infrastructure.changestaging.model.StagedChange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StagedChangesOperationRegistryTest {

	private StagedChangesOperationRegistry testObj;

	@Test
	void getStagedChangesExecutor_shouldReturnMatchingExecutor() {
		// given
		final StagedChangesExecutorInfo stagedChangesExecutorInfo1 = new StagedChangesExecutorInfo(Integer.class,
				ChangeOperation.UPDATE, ChangeTarget.MIRAKL);
		final StagedChangesExecutorInfo stagedChangesExecutorInfo2 = new StagedChangesExecutorInfo(Long.class,
				ChangeOperation.UPDATE, ChangeTarget.MIRAKL);
		final StagedChangesExecutor stagedChangesExecutor1 = new MyStagedChangesExecutor(stagedChangesExecutorInfo1);
		final StagedChangesExecutor stagedChangesExecutor2 = new MyStagedChangesExecutor(stagedChangesExecutorInfo2);
		testObj = new StagedChangesOperationRegistry(List.of(stagedChangesExecutor1, stagedChangesExecutor2));

		// when
		final StagedChangesExecutor result = testObj.getStagedChangesExecutor(
				new StagedChangesExecutorInfo(Integer.class, ChangeOperation.UPDATE, ChangeTarget.MIRAKL));

		// then
		assertThat(result.getExecutorInfo().getType()).isEqualTo(Integer.class);
	}

	@Test
	void getAllStagedChangesExecutorInfo_shouldReturnAllExecutors() {
		// given
		final StagedChangesExecutorInfo stagedChangesExecutorInfo1 = new StagedChangesExecutorInfo(Integer.class,
				ChangeOperation.UPDATE, ChangeTarget.MIRAKL);
		final StagedChangesExecutorInfo stagedChangesExecutorInfo2 = new StagedChangesExecutorInfo(Long.class,
				ChangeOperation.UPDATE, ChangeTarget.MIRAKL);
		final StagedChangesExecutor stagedChangesExecutor1 = new MyStagedChangesExecutor(stagedChangesExecutorInfo1);
		final StagedChangesExecutor stagedChangesExecutor2 = new MyStagedChangesExecutor(stagedChangesExecutorInfo2);
		testObj = new StagedChangesOperationRegistry(List.of(stagedChangesExecutor1, stagedChangesExecutor2));

		// when
		final Set<StagedChangesExecutorInfo> result = testObj.getAllStagedChangesExecutorInfo();

		// then
		assertThat(result).containsExactlyInAnyOrder(stagedChangesExecutorInfo1, stagedChangesExecutorInfo2);
	}

	@Test
	void getAllStagedChangesExecutorInfo_shouldReturnEmpty_whenThereAreNoExecutors() {
		// given
		testObj = new StagedChangesOperationRegistry(List.of());

		// when
		final Set<StagedChangesExecutorInfo> result = testObj.getAllStagedChangesExecutorInfo();

		// then
		assertThat(result).isEmpty();
	}

	private static class MyStagedChangesExecutor implements StagedChangesExecutor {

		private final StagedChangesExecutorInfo stagedChangesExecutorInfo;

		private MyStagedChangesExecutor(final StagedChangesExecutorInfo stagedChangesExecutorInfo) {
			this.stagedChangesExecutorInfo = stagedChangesExecutorInfo;
		}

		@Override
		public void execute(final List<StagedChange> changes) {
			// do nothing for tests
		}

		@Override
		public StagedChangesExecutorInfo getExecutorInfo() {
			return stagedChangesExecutorInfo;
		}

	}

}
