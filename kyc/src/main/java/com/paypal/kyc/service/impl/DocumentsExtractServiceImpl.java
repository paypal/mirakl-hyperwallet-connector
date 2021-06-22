package com.paypal.kyc.service.impl;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.*;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Implementation of interface {@link DocumentsExtractService}
 */
@Service
public class DocumentsExtractServiceImpl implements DocumentsExtractService {

	private final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	private final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	private final HyperwalletSellerExtractService hyperwalletSellerExtractService;

	private final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService;

	private final HMCDocumentsExtractService hmcDocumentsExtractService;

	public DocumentsExtractServiceImpl(final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final HyperwalletSellerExtractService hyperwalletSellerExtractService,
			final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService,
			final HMCDocumentsExtractService hmcDocumentsExtractService) {
		this.miraklSellerDocumentsExtractService = miraklSellerDocumentsExtractService;
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
		this.hyperwalletSellerExtractService = hyperwalletSellerExtractService;
		this.hyperwalletBusinessStakeholderExtractService = hyperwalletBusinessStakeholderExtractService;
		this.hmcDocumentsExtractService = hmcDocumentsExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void extractProofOfIdentityAndBusinessSellerDocuments(final Date delta) {
		final List<KYCDocumentSellerInfoModel> kycDocumentSellerInfoModels = miraklSellerDocumentsExtractService
				.extractProofOfIdentityAndBusinessSellerDocuments(delta);
		final List<KYCDocumentSellerInfoModel> correctlySentDocuments = hyperwalletSellerExtractService
				.pushProofOfIdentityAndBusinessSellerDocuments(kycDocumentSellerInfoModels);
		miraklSellerDocumentsExtractService
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(correctlySentDocuments);
		hmcDocumentsExtractService.cleanUpDocumentsFiles(correctlySentDocuments);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void extractBusinessStakeholderDocuments(final Date delta) {
		final List<KYCDocumentBusinessStakeHolderInfoModel> kycBusinessStakeHolderInfoModels = miraklBusinessStakeholderDocumentsExtractService
				.extractBusinessStakeholderDocuments(delta);
		final List<KYCDocumentBusinessStakeHolderInfoModel> documentsTriedToBeSent = hyperwalletBusinessStakeholderExtractService
				.pushBusinessStakeholderDocuments(kycBusinessStakeHolderInfoModels);
		hyperwalletBusinessStakeholderExtractService.notifyAllDocumentsSentForBstk(documentsTriedToBeSent);
		miraklBusinessStakeholderDocumentsExtractService
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(documentsTriedToBeSent);
		hmcDocumentsExtractService.cleanUpDocumentsFiles(documentsTriedToBeSent);
	}

}
