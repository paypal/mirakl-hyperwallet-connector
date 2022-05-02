package com.paypal.kyc.batchjobs.sellers;

import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SellersDocumentsExtractBatchJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String SELLER_DOCUMENT = "SellerDocument";

	@Test
	void getItemId_ShouldReturnItemId() {

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.clientUserId(CLIENT_USER_ID).build();

		final SellersDocumentsExtractBatchJobItem testObj = new SellersDocumentsExtractBatchJobItem(
				kycDocumentSellerInfoModel);

		assertThat(testObj.getItemId()).isEqualTo(CLIENT_USER_ID);
	}

	@Test
	void getItemType_ShouldReturnItemType() {

		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = KYCDocumentSellerInfoModel.builder()
				.clientUserId(CLIENT_USER_ID).build();

		final SellersDocumentsExtractBatchJobItem testObj = new SellersDocumentsExtractBatchJobItem(
				kycDocumentSellerInfoModel);

		assertThat(testObj.getItemType()).isEqualTo(SELLER_DOCUMENT);
	}

}
