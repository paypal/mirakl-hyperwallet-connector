package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Class that mocks behaviour of sending business stakeholder KYC documents to mockserver
 * instead of sending them to HW
 */

@Profile({ "qa" })
@Slf4j
@Service("hyperwalletBusinessStakeholderExtractService")
public class HyperwalletBusinessStakeholderExtractServiceMockImpl
		extends HyperwalletBusinessStakeholderExtractServiceImpl {

	public HyperwalletBusinessStakeholderExtractServiceMockImpl(HyperwalletSDKService hyperwalletSDKService,
			@Qualifier("hyperwalletDocumentUploadServiceMock") HyperwalletDocumentUploadService hyperwalletDocumentUploadService,
			KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor) {
		super(hyperwalletSDKService, hyperwalletDocumentUploadService,
				kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentExecutor);
	}

}
