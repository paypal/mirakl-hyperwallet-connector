package com.paypal.infrastructure.changestaging.service;

import com.paypal.infrastructure.changestaging.service.operations.StagedChangesExecutorInfo;
import com.paypal.infrastructure.changestaging.service.operations.StagedChangesOperationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StagedChangesPoller {

	private final StagedChangesProcessor stagedChangesProcessor;

	private final ExecutionCounter executionCounter;

	public StagedChangesPoller(final StagedChangesOperationRegistry stagedChangesOperationRegistry,
			final StagedChangesProcessor stagedChangesProcessor) {
		this.executionCounter = new ExecutionCounter(stagedChangesOperationRegistry.getAllStagedChangesExecutorInfo());
		this.stagedChangesProcessor = stagedChangesProcessor;
	}

	@Scheduled(fixedRateString = "${hmc.changestaging.polling-rate}",
			initialDelayString = "${hmc.changestaging.initial-delay}")
	public void performStagedChange() {
		final StagedChangesExecutorInfo stagedChangesExecutorInfo = executionCounter.next();
		stagedChangesProcessor.performStagedChangesFor(stagedChangesExecutorInfo);
	}

	private static class ExecutionCounter {

		private final Map<StagedChangesExecutorInfo, Long> executions;

		public ExecutionCounter(final Set<StagedChangesExecutorInfo> candidates) {
			this.executions = candidates.stream().collect(Collectors.toMap(Function.identity(), e -> 0L));
		}

		public StagedChangesExecutorInfo next() {
			final StagedChangesExecutorInfo next = executions.entrySet().stream().min(Map.Entry.comparingByValue())
					.map(Map.Entry::getKey).orElseThrow(() -> new IllegalStateException("No candidates found"));
			executions.put(next, executions.get(next) + 1);
			return next;
		}

	}

}
