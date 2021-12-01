package com.paypal.kyc.service.impl;

import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.DocumentsExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletBusinessStakeholderExtractService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklBusinessStakeholderDocumentsExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of interface {@link DocumentsExtractService}
 */
@Slf4j
@Service
public class DocumentsExtractServiceImpl implements DocumentsExtractService {

	private final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	private final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService;

	private final HyperwalletSellerExtractService hyperwalletSellerExtractService;

	private final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService;

	public DocumentsExtractServiceImpl(final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final MiraklBusinessStakeholderDocumentsExtractService miraklBusinessStakeholderDocumentsExtractService,
			final HyperwalletSellerExtractService hyperwalletSellerExtractService,
			final HyperwalletBusinessStakeholderExtractService hyperwalletBusinessStakeholderExtractService) {
		this.miraklSellerDocumentsExtractService = miraklSellerDocumentsExtractService;
		this.miraklBusinessStakeholderDocumentsExtractService = miraklBusinessStakeholderDocumentsExtractService;
		this.hyperwalletSellerExtractService = hyperwalletSellerExtractService;
		this.hyperwalletBusinessStakeholderExtractService = hyperwalletBusinessStakeholderExtractService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<KYCDocumentSellerInfoModel> extractProofOfIdentityAndBusinessSellerDocuments(final Date delta) {
		final List<KYCDocumentSellerInfoModel> kycDocumentSellerInfoModels = miraklSellerDocumentsExtractService
				.extractProofOfIdentityAndBusinessSellerDocuments(delta);
		final List<KYCDocumentSellerInfoModel> correctlySentDocuments = hyperwalletSellerExtractService
				.pushProofOfIdentityAndBusinessSellerDocuments(kycDocumentSellerInfoModels);
		miraklSellerDocumentsExtractService
				.setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(correctlySentDocuments);

		return correctlySentDocuments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<KYCDocumentBusinessStakeHolderInfoModel> extractBusinessStakeholderDocuments(final Date delta) {
		final List<KYCDocumentBusinessStakeHolderInfoModel> kycBusinessStakeHolderInfoModels = miraklBusinessStakeholderDocumentsExtractService
				.extractBusinessStakeholderDocuments(delta);
		final List<KYCDocumentBusinessStakeHolderInfoModel> correctlySentDocuments = hyperwalletBusinessStakeholderExtractService
				.pushBusinessStakeholderDocuments(kycBusinessStakeHolderInfoModels);
		miraklBusinessStakeholderDocumentsExtractService
				.setBusinessStakeholderFlagKYCToPushBusinessStakeholderDocumentsToFalse(correctlySentDocuments);

		return correctlySentDocuments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("java:S3864")
	public <T extends KYCDocumentInfoModel> void cleanUpDocumentsFiles(final List<T> successFullPushedListOfDocuments) {
		log.info("Cleaning up files from disk...");
		//@formatter:off
		CollectionUtils.emptyIfNull(successFullPushedListOfDocuments).stream()
				.map(T::getDocuments)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.map(KYCDocumentModel::getFile)
				.filter(Objects::nonNull)
				.peek(file -> log.info("File selected to be deleted [{}]", file.getAbsolutePath()))
				.forEach(File::delete);
		//@formatter:on
		log.info("Cleaning up done successfully!");
	}

}
