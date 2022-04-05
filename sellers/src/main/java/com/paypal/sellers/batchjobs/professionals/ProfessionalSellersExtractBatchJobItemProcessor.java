package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import org.springframework.stereotype.Service;

/**
 * Sellers extract batch job item processor for creating the users in HW.
 */
@Service
public class ProfessionalSellersExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor;

	public ProfessionalSellersExtractBatchJobItemProcessor(
			final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor) {
		this.hyperWalletUserServiceStrategyExecutor = hyperWalletUserServiceStrategyExecutor;
	}

	/**
	 * Processes the {@link ProfessionalSellerExtractJobItem} with the
	 * {@link HyperWalletUserServiceStrategyExecutor}.
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link ProfessionalSellerExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final ProfessionalSellerExtractJobItem jobItem) {
		hyperWalletUserServiceStrategyExecutor.execute(jobItem.getItem());
	}

}
