package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The extractor class for retrieving all the sellers within a delta time from mirakl
 */
@Service
public class BankAccountExtractBatchJobItemsExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public BankAccountExtractBatchJobItemsExtractor(final MiraklSellersExtractService miraklSellersExtractService) {
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * Retrieves all the sellers modified since the {@code delta} time and returns them as
	 * a {@link BankAccountExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link BankAccountExtractJobItem}
	 */
	@Override
	protected Collection<BankAccountExtractJobItem> getItems(Date delta) {
		Collection<BankAccountExtractJobItem> individualBankAccounts = miraklSellersExtractService
				.extractIndividuals(delta).stream().map(BankAccountExtractJobItem::new).collect(Collectors.toList());
		Collection<BankAccountExtractJobItem> professionalBankAccounts = miraklSellersExtractService
				.extractProfessionals(delta).stream().map(BankAccountExtractJobItem::new).collect(Collectors.toList());

		return Stream.concat(individualBankAccounts.stream(), professionalBankAccounts.stream())
				.collect(Collectors.toList());
	}

}
