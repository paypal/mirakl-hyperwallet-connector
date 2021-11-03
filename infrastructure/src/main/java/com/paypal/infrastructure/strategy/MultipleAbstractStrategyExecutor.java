package com.paypal.infrastructure.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract strategy executor that ensures that multiple strategies run for an specific
 * source and target
 */
public abstract class MultipleAbstractStrategyExecutor<S, T> implements StrategyExecutor<S, List<T>> {

	@Override
	public List<T> execute(final S source) {
		final ArrayList<T> appendedExecution = new ArrayList<>();
		final Set<Strategy<S, T>> strategies = getStrategies();
		if (Objects.nonNull(strategies)) {
			for (final Strategy<S, T> strategy : strategies) {
				if (strategy.isApplicable(source)) {
					appendedExecution.add(strategy.execute(source));
				}
			}
		}
		return appendedExecution;
	}

	protected abstract Set<Strategy<S, T>> getStrategies();

}
