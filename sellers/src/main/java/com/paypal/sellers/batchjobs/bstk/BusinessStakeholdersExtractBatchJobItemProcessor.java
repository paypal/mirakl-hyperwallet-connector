package com.paypal.sellers.batchjobs.bstk;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.sellers.sellersextract.model.BusinessStakeHolderModel;
import com.paypal.sellers.sellersextract.service.MiraklBusinessStakeholderExtractService;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletBusinessStakeHolderStrategyExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business stakeholders extract batch job item processor for creating the users in HW.
 */
@Service
public class BusinessStakeholdersExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, BusinessStakeholderExtractJobItem> {

	private final HyperWalletBusinessStakeHolderStrategyExecutor hyperWalletBusinessStakeHolderStrategyExecutor;

	private final MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractService;

	public BusinessStakeholdersExtractBatchJobItemProcessor(
			final HyperWalletBusinessStakeHolderStrategyExecutor hyperWalletBusinessStakeHolderStrategyExecutor,
			final MiraklBusinessStakeholderExtractService miraklBusinessStakeholderExtractService) {
		this.hyperWalletBusinessStakeHolderStrategyExecutor = hyperWalletBusinessStakeHolderStrategyExecutor;
		this.miraklBusinessStakeholderExtractService = miraklBusinessStakeholderExtractService;
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
		final BusinessStakeHolderModel businessStakeHolderModel = hyperWalletBusinessStakeHolderStrategyExecutor
				.execute(jobItem.getItem());
		if (businessStakeHolderModel != null) {
			miraklBusinessStakeholderExtractService.updateBusinessStakeholderToken(
					businessStakeHolderModel.getClientUserId(), List.of(businessStakeHolderModel));
		}
	}

}
