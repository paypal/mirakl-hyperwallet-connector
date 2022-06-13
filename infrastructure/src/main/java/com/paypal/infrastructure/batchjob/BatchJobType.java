package com.paypal.infrastructure.batchjob;

/**
 * Batch Job types
 */
public enum BatchJobType {

	/**
	 * Extract batch jobs extract items from Mirakl so they can be pushed/updated into
	 * Hyperwallet
	 */
	EXTRACT,

	/**
	 * Retry batch jobs take items that have failed in previous job executions and try to
	 * process them again.
	 */
	RETRY

}
