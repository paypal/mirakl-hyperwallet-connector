package com.paypal.jobsystem.batchjob.model;

/**
 * List of all possible item validation outcomes.
 *
 * <ul>
 * <li>VALID: the item is correct and processing will continue.</li>
 * <li>INVALID: the item is not valid so the item is not going to be processed and added
 * for future retry.</li>
 * <li>WARNING: the item is not valid but the processing will continue even if it can
 * potentially fail.</li>
 * </ul>
 */
public enum BatchJobItemValidationStatus {

	VALID, INVALID, WARNING

}
