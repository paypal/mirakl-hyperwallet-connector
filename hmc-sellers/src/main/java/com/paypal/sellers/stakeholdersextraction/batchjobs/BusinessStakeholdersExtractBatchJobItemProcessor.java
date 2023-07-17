package com.paypal.sellers.stakeholdersextraction.batchjobs;

import com.paypal.infrastructure.support.services.TokenSynchronizationService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.sellers.stakeholdersextraction.model.BusinessStakeHolderModel;
import com.paypal.sellers.stakeholdersextraction.services.strategies.HyperWalletBusinessStakeHolderStrategyExecutor;
import org.springframework.stereotype.Component;

/**
 * Business stakeholders extract batch job item processor for creating the users in HW.
 */
@Component
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
