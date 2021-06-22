package com.paypal.infrastructure.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract strategy factory that ensures that multiple strategies run for an specific
 * source and target
 */
public abstract class MultipleAbstractStrategyFactory<S, T> implements StrategyFactory<S, List<T>> {

	@Override
	public List<T> execute(final S source) {
		final ArrayList<T> appendedExecution = new ArrayList<>();
		if (Objects.nonNull(getStrategies()) && !getStrategies().isEmpty()) {
			for (final Strategy<S, T> strategy : getStrategies()) {
				if (strategy.isApplicable(source)) {
					appendedExecution.add(strategy.execute(source));
				}
			}
		}
		return appendedExecution;
	}

	protected abstract Set<Strategy<S, T>> getStrategies();

}
