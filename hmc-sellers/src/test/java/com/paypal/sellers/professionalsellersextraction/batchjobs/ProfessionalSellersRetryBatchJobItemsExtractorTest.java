package com.paypal.sellers.professionalsellersextraction.batchjobs;

import com.paypal.jobsystem.batchjob.model.BatchJobItem;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellerExtractJobItem;
import com.paypal.sellers.professionalsellersextraction.batchjobs.ProfessionalSellersRetryBatchJobItemsExtractor;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
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
class ProfessionalSellersRetryBatchJobItemsExtractorTest {

	private static final String SELLER_ID_1 = "1";

	private static final String SELLER_ID_2 = "2";

	@InjectMocks
	private ProfessionalSellersRetryBatchJobItemsExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private SellerModel sellerModelMock1, sellerModelMock2;

	@Test
	void getItem_shouldReturnProfessionalSellerType() {
		final String result = testObj.getItemType();

		assertThat(result).isEqualTo(ProfessionalSellerExtractJobItem.ITEM_TYPE);
	}

	@SuppressWarnings("uncheked")
	@Test
	void getItems_ShouldReturnAllProfessionalSellersByTheGivenIds() {
		when(miraklSellersExtractServiceMock.extractProfessionals(List.of(SELLER_ID_1, SELLER_ID_2)))
				.thenReturn(List.of(sellerModelMock1, sellerModelMock2));

		final Collection<ProfessionalSellerExtractJobItem> result = testObj.getItems(List.of(SELLER_ID_1, SELLER_ID_2));

		assertThat(result.stream().map(BatchJobItem::getItem).map(SellerModel.class::cast))
				.containsExactlyInAnyOrder(sellerModelMock1, sellerModelMock2);

	}

}
