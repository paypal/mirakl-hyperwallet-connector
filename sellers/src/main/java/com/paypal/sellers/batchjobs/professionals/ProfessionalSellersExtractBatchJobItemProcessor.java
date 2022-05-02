package com.paypal.sellers.batchjobs.professionals;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.service.TokenSynchronizationService;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.strategies.HyperWalletUserServiceStrategyExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Sellers extract batch job item processor for creating the users in HW.
 */
@Service
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
