package com.paypal.infrastructure.batchjob.listeners;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import org.springframework.stereotype.Component;

/**
 * Audit batch job processing listener.
 */
@Component
public class AuditBatchJobItemProcessingListener
		extends AbstractBatchJobProcessingListenerSupport<BatchJobContext, BatchJobItem<?>> {

}
