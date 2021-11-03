package com.paypal.infrastructure.strategy;

import java.util.Objects;
import java.util.Set;

/**
 * Abstract strategy executor that ensures that only one strategy is run for an specific
 * source and target
 */
public abstract class SingleAbstractStrategyExecutor<S, T> implements StrategyExecutor<S, T> {

	@Override
	public T execute(final S source) {
		if (Objects.nonNull(getStrategies()) && !getStrategies().isEmpty()) {
			for (final Strategy<S, T> strategy : getStrategies()) {
				if (strategy.isApplicable(source)) {
					return strategy.execute(source);
				}
			}
		}
		return null;
	}

	protected abstract Set<Strategy<S, T>> getStrategies();

}
