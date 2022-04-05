package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import org.springframework.stereotype.Component;

/**
 * Individual sellers extract batch job item processor for creating the users in HW
 */
@Component
public class IndividualSellersExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, IndividualSellerExtractJobItem> {

	private final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor;

	public IndividualSellersExtractBatchJobItemProcessor(
			final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor) {
		this.hyperWalletUserServiceStrategyExecutor = hyperWalletUserServiceStrategyExecutor;
	}

	/**
	 * Processes the {@link IndividualSellerExtractJobItem} with the
	 * {@link HyperWalletUserServiceStrategyExecutor}
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link IndividualSellerExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final IndividualSellerExtractJobItem jobItem) {
		hyperWalletUserServiceStrategyExecutor.execute(jobItem.getItem());
	}

}
