package com.paypal.kyc.batchjobs.sellers;

import com.paypal.infrastructure.batchjob.AbstractDeltaBatchJobItemsExtractor;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Handles the extraction of seller documents. It retrieves all documents from shops that
 * have been modified in Mirakl.
 */
@Service
public class SellersDocumentsExtractBatchJobItemExtractor
		extends AbstractDeltaBatchJobItemsExtractor<BatchJobContext, SellersDocumentsExtractBatchJobItem> {

	private final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	public SellersDocumentsExtractBatchJobItemExtractor(
			final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService) {
		this.miraklSellerDocumentsExtractService = miraklSellerDocumentsExtractService;
	}

	/**
	 * Retrieves all the seller documents modified since the {@code delta} time and
	 * returns them as a {@link Collection} of {@link SellersDocumentsExtractBatchJobItem}
	 * @param delta the cut-out {@link Date}
	 * @return a {@link Collection} of {@link SellersDocumentsExtractBatchJobItem}
	 */
	@Override
	protected Collection<SellersDocumentsExtractBatchJobItem> getItems(final Date delta) {
		return miraklSellerDocumentsExtractService.extractProofOfIdentityAndBusinessSellerDocuments(delta).stream()
				.map(SellersDocumentsExtractBatchJobItem::new).collect(Collectors.toList());
	}

}
