package com.paypal.infrastructure.batchjob;

/**
 * Batch job status.
 */
public enum BatchJobStatus {

	NOT_STARTED, RUNNING, RUNNING_WITH_FAILURES, FINISHED, FINISHED_WITH_FAILURES, FAILED, ABORTED

}
