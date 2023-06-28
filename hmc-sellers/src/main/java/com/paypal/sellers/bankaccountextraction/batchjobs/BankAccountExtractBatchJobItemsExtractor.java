package com.paypal.sellers.bankaccountextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobContext;
import com.paypal.jobsystem.batchjobaudit.services.BatchJobTrackingService;
import com.paypal.jobsystem.batchjobsupport.support.AbstractDynamicWindowDeltaBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The extractor class for retrieving all the sellers within a delta time from mirakl
 */
@Component
public class BankAccountExtractBatchJobItemsExtractor
		extends AbstractDynamicWindowDeltaBatchJobItemsExtractor<BatchJobContext, BankAccountExtractJobItem> {

	private final MiraklSellersExtractService miraklSellersExtractService;

	public BankAccountExtractBatchJobItemsExtractor(final MiraklSellersExtractService miraklSellersExtractService,
			final BatchJobTrackingService batchJobTrackingService) {
		super(batchJobTrackingService);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * Retrieves all the sellers modified since the {@code delta} time and returns them as
	 * a {@link BankAccountExtractJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link BankAccountExtractJobItem}
	 */
	@Override
	protected Collection<BankAccountExtractJobItem> getItems(final BatchJobContext ctx, final Date delta) {
		final Collection<BankAccountExtractJobItem> individualBankAccounts = miraklSellersExtractService
				.extractIndividuals(delta).stream().map(BankAccountExtractJobItem::new).collect(Collectors.toList());
		final Collection<BankAccountExtractJobItem> professionalBankAccounts = miraklSellersExtractService
				.extractProfessionals(delta).stream().map(BankAccountExtractJobItem::new).collect(Collectors.toList());

		return Stream.concat(individualBankAccounts.stream(), professionalBankAccounts.stream())
				.collect(Collectors.toList());
	}

}
