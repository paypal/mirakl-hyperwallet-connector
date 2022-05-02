package com.paypal.kyc.batchjobs.businessstakeholders;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessStakeholdersDocumentsExtractBatchJobItemTest {

	private static final String CLIENT_USER_ID = "clientUserId";

	private static final String BUSINESSSTAKEHOLDER_TOKEN = "BusinessStakeholderToken";

	private static final Integer BUSINESSSTAKEHOLDER_NUMBER = 0;

	private static final String SELLER_DOCUMENT = "BusinessStakeholderDocument";

	@Test
	void getItemId_ShouldReturnItemId() {

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentSellerInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().clientUserId(CLIENT_USER_ID).businessStakeholderMiraklNumber(BUSINESSSTAKEHOLDER_NUMBER)
				.token(BUSINESSSTAKEHOLDER_TOKEN).build();

		final BusinessStakeholdersDocumentsExtractBatchJobItem testObj = new BusinessStakeholdersDocumentsExtractBatchJobItem(
				kycDocumentSellerInfoModel);

		assertThat(testObj.getItemId())
				.isEqualTo(CLIENT_USER_ID + "-" + BUSINESSSTAKEHOLDER_NUMBER + "-" + BUSINESSSTAKEHOLDER_TOKEN);
	}

	@Test
	void getItemType_ShouldReturnItemType() {

		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentSellerInfoModel = KYCDocumentBusinessStakeHolderInfoModel
				.builder().clientUserId(CLIENT_USER_ID).build();

		final BusinessStakeholdersDocumentsExtractBatchJobItem testObj = new BusinessStakeholdersDocumentsExtractBatchJobItem(
				kycDocumentSellerInfoModel);

		assertThat(testObj.getItemType()).isEqualTo(SELLER_DOCUMENT);
	}

}
