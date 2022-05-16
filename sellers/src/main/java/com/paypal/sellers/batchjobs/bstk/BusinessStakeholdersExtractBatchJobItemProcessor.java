package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.service.TokenSynchronizationService;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletBusinessStakeHolderStrategyExecutor;
import org.springframework.stereotype.Service;

/**
 * Business stakeholders extract batch job item processor for creating the users in HW.
 */
@Service
public class BusinessStakeholdersExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final HyperWalletBusinessStakeHolderStrategyExecutor hyperWalletBusinessStakeHolderStrategyExecutor;

	private final TokenSynchronizationService<BusinessStakeHolderModel> businessStakeholderTokenSynchronizationService;

	public BusinessStakeholdersExtractBatchJobItemProcessor(
			final HyperWalletBusinessStakeHolderStrategyExecutor hyperWalletBusinessStakeHolderStrategyExecutor,
			final TokenSynchronizationService<BusinessStakeHolderModel> businessStakeholderTokenSynchronizationService) {
		this.hyperWalletBusinessStakeHolderStrategyExecutor = hyperWalletBusinessStakeHolderStrategyExecutor;
		this.businessStakeholderTokenSynchronizationService = businessStakeholderTokenSynchronizationService;
	}

	/**
	 * Processes the {@link BusinessStakeholderExtractJobItem} with the
	 * {@link HyperWalletBusinessStakeHolderStrategyExecutor} and update the executed
	 * {@link BusinessStakeHolderModel}.
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link BusinessStakeholderExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final BusinessStakeholderExtractJobItem jobItem) {

		final BusinessStakeHolderModel synchronizedBusinessStakeHolderModel = businessStakeholderTokenSynchronizationService
				.synchronizeToken(jobItem.getItem());

		hyperWalletBusinessStakeHolderStrategyExecutor.execute(synchronizedBusinessStakeHolderModel);
	}

}
