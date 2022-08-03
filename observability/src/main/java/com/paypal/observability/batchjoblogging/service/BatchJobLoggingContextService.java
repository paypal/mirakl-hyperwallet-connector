package com.paypal.observability.batchjoblogging.service;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;

public interface BatchJobLoggingContextService {

	void refreshBatchJobInformation(BatchJobContext batchJobContext);

	void refreshBatchJobInformation(BatchJobContext batchJobContext, BatchJobItem<?> item);

	void removeBatchJobItemInformation();

	void removeBatchJobInformation();

}
