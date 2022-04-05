package com.paypal.sellers.batchjobs.professionals;

import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProfessionalSellerExtractJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String PROFESSIONAL_SELLER = "ProfessionalSeller";

	@Test
	void getItemId_ShouldReturnClientUserId() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();

		final ProfessionalSellerExtractJobItem testObj = new ProfessionalSellerExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID);
	}

	@Test
	void getItemType_ShouldReturnProfessionalSeller() {

		final SellerModel sellerModel = SellerModel.builder().clientUserId(CLIENT_USER_ID).build();

		final ProfessionalSellerExtractJobItem testObj = new ProfessionalSellerExtractJobItem(sellerModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID);
		assertThat(testObj.getItemType()).isEqualTo(PROFESSIONAL_SELLER);
	}

}