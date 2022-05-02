package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountExtractBatchJobItemsExtractorTest {

	private static final Date DELTA = new Date();

	@InjectMocks
	private BankAccountExtractBatchJobItemsExtractor testObj;

	@Mock
	private MiraklSellersExtractService miraklSellersExtractServiceMock;

	@Mock
	private SellerModel sellerModelMock1, sellerModelMock2, sellerModelMock3, sellerModelMock4;

	@Test
	void getItems_ShouldRetrieveAllBankAccountExtractJobItemForTheGivenDelta() {

		when(miraklSellersExtractServiceMock.extractIndividuals(DELTA))
				.thenReturn(List.of(sellerModelMock1, sellerModelMock2));
		when(miraklSellersExtractServiceMock.extractProfessionals(DELTA))
				.thenReturn(List.of(sellerModelMock3, sellerModelMock4));

		final Collection<BankAccountExtractJobItem> result = testObj.getItems(DELTA);

		assertThat(result.stream().map(BankAccountExtractJobItem::getItem)).containsExactlyInAnyOrder(sellerModelMock1,
				sellerModelMock2, sellerModelMock3, sellerModelMock4);
	}

}
