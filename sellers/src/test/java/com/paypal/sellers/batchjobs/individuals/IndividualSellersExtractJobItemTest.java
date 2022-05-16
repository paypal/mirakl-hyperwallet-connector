package com.paypal.sellers.batchjobs.individuals;

import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IndividualSellersExtractJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String INDIVIDUAL_SELLER = "IndividualSeller";

	@Test
	void getItemId_ShouldReturnItemId() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();

		final IndividualSellersExtractJobItem testObj = new IndividualSellersExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID);
	}

	@Test
	void getItemType_ShouldReturnItemType() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();

		final IndividualSellersExtractJobItem testObj = new IndividualSellersExtractJobItem(sellerModel);

		assertThat(testObj.getItemType()).isEqualTo(INDIVIDUAL_SELLER);
	}

}
