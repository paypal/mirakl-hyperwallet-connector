package com.paypal.infrastructure.changestaging.service.operations;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StagedChangesOperationRegistry {

	private final Map<StagedChangesExecutorInfo, StagedChangesExecutor> stringStagedChangesOperationsMap;

	public StagedChangesOperationRegistry(final List<StagedChangesExecutor> stagedChangesExecutors) {
		this.stringStagedChangesOperationsMap = stagedChangesExecutors.stream()
				.collect(Collectors.toMap(StagedChangesExecutor::getExecutorInfo, Function.identity()));
	}

	public StagedChangesExecutor getStagedChangesExecutor(final StagedChangesExecutorInfo stagedChangesExecutorInfo) {
		return stringStagedChangesOperationsMap.get(stagedChangesExecutorInfo);
	}

	public Set<StagedChangesExecutorInfo> getAllStagedChangesExecutorInfo() {
		return stringStagedChangesOperationsMap.keySet();
	}

}
