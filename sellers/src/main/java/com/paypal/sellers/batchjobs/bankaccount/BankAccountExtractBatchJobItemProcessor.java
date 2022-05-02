package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.infrastructure.service.TokenSynchronizationService;
import com.paypal.sellers.bankaccountextract.service.strategies.HyperWalletBankAccountStrategyExecutor;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Bank account extract batch job item processor for creating the bank accounts in HW
 */
@Service
public class BankAccountExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, BankAccountExtractJobItem> {

	private final HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutor;

	private final TokenSynchronizationService<SellerModel> bankAccountTokenSynchronizationService;

	public BankAccountExtractBatchJobItemProcessor(
			HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutor,
			TokenSynchronizationService<SellerModel> bankAccountTokenSynchronizationService) {
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
