package com.paypal.infrastructure.strategy;

/**
 * Generic interface for executing strategies
 */
public interface StrategyExecutor<S, T> {

	/**
	 * Executes business logic based on the {@code source} and returns the target
	 * {@link T} class based on a set of strategies
	 * @param source the source object of type {@link S}
	 * @return the returned object of type {@link T}
	 */
	T execute(S source);

}
