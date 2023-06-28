package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.infrastructure.support.services.TokenSynchronizationService;
import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobsupport.model.BatchJobItemProcessor;
import com.paypal.sellers.bankaccountextraction.services.strategies.HyperWalletBankAccountStrategyExecutor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.springframework.stereotype.Component;

/**
 * Bank account extract batch job item processor for creating the bank accounts in HW
 */
@Component
public class BankAccountExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, BankAccountExtractJobItem> {

	private final HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutor;

	private final TokenSynchronizationService<SellerModel> bankAccountTokenSynchronizationService;

	public BankAccountExtractBatchJobItemProcessor(
			final HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutor,
			final TokenSynchronizationService<SellerModel> bankAccountTokenSynchronizationService) {
		this.hyperWalletBankAccountStrategyExecutor = hyperWalletBankAccountStrategyExecutor;
		this.bankAccountTokenSynchronizationService = bankAccountTokenSynchronizationService;
	}

	/**
	 * Processes the {@link BankAccountExtractJobItem} with the
	 * {@link HyperWalletBankAccountStrategyExecutor}
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link BankAccountExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final BankAccountExtractJobItem jobItem) {
		final SellerModel synchronizedSellerModel = bankAccountTokenSynchronizationService
				.synchronizeToken(jobItem.getItem());
		hyperWalletBankAccountStrategyExecutor.execute(synchronizedSellerModel);
	}

}
