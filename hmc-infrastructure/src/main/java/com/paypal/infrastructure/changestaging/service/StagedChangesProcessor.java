package com.paypal.infrastructure.changestaging.service;

import com.paypal.infrastructure.changestaging.repositories.StagedChangesRepository;
import com.paypal.infrastructure.changestaging.repositories.entities.StagedChangeEntity;
import com.paypal.infrastructure.changestaging.service.converters.StagedChangesEntityConverter;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesExecutor;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesExecutorInfo;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesOperationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Transactional
public class StagedChangesProcessor {

	@Value("${hmc.changestaging.batch-size}")
	private int operationBatchSize;

	private final StagedChangesRepository stagedChangesRepository;

	private final StagedChangesEntityConverter stagedChangesEntityConverter;

	private final StagedChangesOperationRegistry stagedChangesOperationRegistry;

	public StagedChangesProcessor(final StagedChangesRepository stagedChangesRepository,
			final StagedChangesEntityConverter stagedChangesEntityConverter,
			final StagedChangesOperationRegistry stagedChangesOperationRegistry) {
		this.stagedChangesRepository = stagedChangesRepository;
		this.stagedChangesEntityConverter = stagedChangesEntityConverter;
		this.stagedChangesOperationRegistry = stagedChangesOperationRegistry;
	}

	public void performStagedChangesFor(final StagedChangesExecutorInfo stagedChangesExecutorInfo) {
		final List<StagedChangeEntity> stagedChanges = retrieveStagedChanges(stagedChangesExecutorInfo);

		if (!stagedChanges.isEmpty()) {
			log.info("Executing {} staged changes of type {} for target {}", stagedChanges.size(),
					stagedChangesExecutorInfo.getType().getName(), stagedChangesExecutorInfo.getTarget());
			processStagedChanges(stagedChangesExecutorInfo, stagedChanges);
		}
	}

	private List<StagedChangeEntity> retrieveStagedChanges(final StagedChangesExecutorInfo stagedChangesExecutorInfo) {
		//@formatter:off
		return stagedChangesRepository.findByTypeAndOperationAndTargetOrderByCreationDateAsc(
				stagedChangesExecutorInfo.getType().getName(),
				stagedChangesExecutorInfo.getOperation(),
				stagedChangesExecutorInfo.getTarget(),
				Pageable.ofSize(operationBatchSize));
		//@formatter:on
	}

	private void processStagedChanges(final StagedChangesExecutorInfo stagedChangesExecutorInfo,
			final List<StagedChangeEntity> stagedChanges) {
		final StagedChangesExecutor stagedChangesExecutor = stagedChangesOperationRegistry
				.getStagedChangesExecutor(stagedChangesExecutorInfo);

		try {
			stagedChangesExecutor.execute(stagedChangesEntityConverter.from(stagedChanges));
		}
		finally {
			final List<String> stagedChangeIds = stagedChanges.stream().map(StagedChangeEntity::getId).toList();

			stagedChangesRepository.deleteByIdIn(stagedChangeIds);
		}
	}

}
