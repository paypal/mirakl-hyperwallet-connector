package com.paypal.observability.batchjoblogging.service;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjob.model.BatchJobItem;

public interface BatchJobLoggingContextService {

	void refreshBatchJobInformation(BatchJobContext batchJobContext);

	void refreshBatchJobInformation(BatchJobContext batchJobContext, BatchJobItem<?> item);

	void removeBatchJobItemInformation();

	void removeBatchJobInformation();

}
