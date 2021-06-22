package com.paypal.infrastructure.strategy;

/**
 * Strategy interface
 */
public interface Strategy<S, T> {

	/**
	 * Executes the business logic based on the content of {@code source} and returns a
	 * {@link T} class based on a set of strategies
	 * @param source the source object of type {@link S}
	 * @return the converted object of type {@link T}
	 */
	T execute(S source);

	/**
	 * Checks whether the strategy must be executed based on the {@code source}
	 * @param source the source object
	 * @return returns whether the strategy is applicable or not
	 */
	boolean isApplicable(S source);

}
