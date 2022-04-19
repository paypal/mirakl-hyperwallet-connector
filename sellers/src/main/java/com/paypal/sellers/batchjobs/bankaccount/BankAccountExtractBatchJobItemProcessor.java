package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItemProcessor;
import com.paypal.sellers.bankaccountextract.service.strategies.HyperWalletBankAccountStrategyExecutor;
import org.springframework.stereotype.Service;

/**
 * Bank account extract batch job item processor for creating the bank accounts in HW
 */
@Service
public class BankAccountExtractBatchJobItemProcessor
		implements BatchJobItemProcessor<BatchJobContext, BankAccountExtractJobItem> {

	private final HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutor;

	public BankAccountExtractBatchJobItemProcessor(
			final HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutor) {
		this.hyperWalletBankAccountStrategyExecutor = hyperWalletBankAccountStrategyExecutor;
	}

	/**
	 * Processes the {@link BankAccountExtractJobItem} with the
	 * {@link HyperWalletBankAccountStrategyExecutor}
	 * @param ctx The {@link BatchJobContext}
	 * @param jobItem The {@link BankAccountExtractJobItem}
	 */
	@Override
	public void processItem(final BatchJobContext ctx, final BankAccountExtractJobItem jobItem) {
		hyperWalletBankAccountStrategyExecutor.execute(jobItem.getItem());
	}

}
