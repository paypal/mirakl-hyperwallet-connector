package com.paypal.observability.batchjoblogging.service;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.observability.batchjoblogging.model.BatchJobLoggingTransaction;
import com.paypal.observability.loggingcontext.model.LoggingTransaction;
import com.paypal.observability.loggingcontext.service.LoggingContextService;
import org.springframework.stereotype.Component;

@Component
public class BatchJobLoggingContextServiceImpl implements BatchJobLoggingContextService {

	private final LoggingContextService loggingContextService;

	public BatchJobLoggingContextServiceImpl(final LoggingContextService loggingContextService) {
		this.loggingContextService = loggingContextService;
	}

	@Override
	public void refreshBatchJobInformation(final BatchJobContext batchJobContext) {
		final BatchJobLoggingTransaction businessTransactionInfo = getOrCreateBatchJobBusinessTransactionInfo(
				batchJobContext);
		businessTransactionInfo.setItemType(null);
		businessTransactionInfo.setItemId(null);
		businessTransactionInfo.setSubtype(batchJobContext.getJobName());

		loggingContextService.updateLoggingTransaction(businessTransactionInfo);
	}

	@Override
	public void refreshBatchJobInformation(final BatchJobContext batchJobContext, final BatchJobItem<?> item) {
		final BatchJobLoggingTransaction businessTransactionInfo = getOrCreateBatchJobBusinessTransactionInfo(
				batchJobContext);
		businessTransactionInfo.setItemType(item.getItemType());
		businessTransactionInfo.setItemId(item.getItemId());

		loggingContextService.updateLoggingTransaction(businessTransactionInfo);
	}

	@Override
	public void removeBatchJobItemInformation() {
		final BatchJobLoggingTransaction businessTransactionInfo = getOrCreateBatchJobBusinessTransactionInfo();
		if (businessTransactionInfo != null) {
			businessTransactionInfo.setItemType(null);
			businessTransactionInfo.setItemId(null);

			loggingContextService.updateLoggingTransaction(businessTransactionInfo);
		}
	}

	@Override
	public void removeBatchJobInformation() {
		loggingContextService.closeLoggingTransaction();
	}

	private BatchJobLoggingTransaction getOrCreateBatchJobBusinessTransactionInfo(
			final BatchJobContext batchJobContext) {
		//@formatter:off
		return (BatchJobLoggingTransaction) loggingContextService.getCurrentLoggingTransaction()
				.orElseGet(() -> createNewJobTransaction(batchJobContext));
		//@formatter:on
	}

	private BatchJobLoggingTransaction createNewJobTransaction(final BatchJobContext batchJobContext) {
		final BatchJobLoggingTransaction newTransaction = new BatchJobLoggingTransaction(batchJobContext.getJobUuid(),
				batchJobContext.getJobName());
		loggingContextService.updateLoggingTransaction(newTransaction);
		return newTransaction;
	}

	private BatchJobLoggingTransaction getOrCreateBatchJobBusinessTransactionInfo() {
		final LoggingTransaction loggingTransaction = loggingContextService.getCurrentLoggingTransaction().orElse(null);
		return loggingTransaction != null ? (BatchJobLoggingTransaction) loggingTransaction : null;
	}

}
