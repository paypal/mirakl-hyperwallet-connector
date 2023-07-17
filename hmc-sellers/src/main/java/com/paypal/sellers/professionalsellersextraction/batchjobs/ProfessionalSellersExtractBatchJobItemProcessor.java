package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.infrastructure.support.services.TokenSynchronizationService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.strategies.HyperWalletUserServiceStrategyExecutor;
import org.springframework.stereotype.Component;

/**
 * Sellers extract batch job item processor for creating the users in HW.
 */
@Component
public class ProfessionalSellersExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, ProfessionalSellerExtractJobItem> {

	private final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor;

	private final TokenSynchronizationService<SellerModel> sellersTokenSynchronizationService;

	public ProfessionalSellersExtractBatchJobItemProcessor(
			final HyperWalletUserServiceStrategyExecutor hyperWalletUserServiceStrategyExecutor,
			final TokenSynchronizationService<SellerModel> sellersTokenSynchronizationService) {
		this.hyperWalletUserServiceStrategyExecutor = hyperWalletUserServiceStrategyExecutor;
		this.sellersTokenSynchronizationService = sellersTokenSynchronizationService;
	}

	/**
	 * Processes the {@link ProfessionalSellerExtractJobItem} with the
	 * {@link HyperWalletUserServiceStrategyExecutor}.
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link ProfessionalSellerExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final ProfessionalSellerExtractJobItem jobItem) {
		final SellerModel sellerModel = sellersTokenSynchronizationService.synchronizeToken(jobItem.getItem());
		hyperWalletUserServiceStrategyExecutor.execute(sellerModel);
	}

}
