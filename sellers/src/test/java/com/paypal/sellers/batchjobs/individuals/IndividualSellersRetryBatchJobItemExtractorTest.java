package com.paypal.sellers.batchjobs.individuals;

import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualSellersRetryBatchJobItemExtractorTest {

	private static final String SELLER_ID_1 = "1";

	private static final String SELLER_ID_2 = "2";

	@InjectMocks
	private IndividualSellersRetryBatchJobItemExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private SellerModel sellerModelMock1, sellerModelMock2;

	@Test
	void getItems_ShouldReturnAllSellersByTheGivenIds() {

		when(miraklSellersExtractServiceMock.extractSellers(List.of(SELLER_ID_1, SELLER_ID_2)))
				.thenReturn(List.of(sellerModelMock1, sellerModelMock2));

		final Collection<IndividualSellersExtractJobItem> result = testObj.getItems(List.of(SELLER_ID_1, SELLER_ID_2));

		assertThat(result.stream().map(BatchJobItem::getItem).map(SellerModel.class::cast))
				.containsExactlyInAnyOrder(sellerModelMock1, sellerModelMock2);
	}

}
