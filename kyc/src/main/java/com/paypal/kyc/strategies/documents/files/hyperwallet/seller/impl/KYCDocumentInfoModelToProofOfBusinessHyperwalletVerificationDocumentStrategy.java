package com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentCategoryEnum;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KYCDocumentInfoModelToProofOfBusinessHyperwalletVerificationDocumentStrategy
		implements Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletVerificationDocument execute(final KYCDocumentSellerInfoModel source) {
	//@formatter:off
		final Map<String, String> uploadFiles = source.getProofOfBusinessDocuments()
				.stream()
				.collect(Collectors.toMap(kycDocumentModel -> source.getProofOfBusiness()
						.name()
						.toLowerCase() + '_' + kycDocumentModel
						.getDocumentSide()
						.name()
						.toLowerCase(), kycDocumentModel -> kycDocumentModel.getFile()
						.getAbsolutePath()));
		//@formatter:on

		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setType(source.getProofOfBusiness().name());
		hyperwalletVerificationDocument.setCategory(KYCDocumentCategoryEnum.BUSINESS.name());
		hyperwalletVerificationDocument.setUploadFiles(uploadFiles);

		return hyperwalletVerificationDocument;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCDocumentSellerInfoModel source) {
		return source.isProfessional() && Objects.nonNull(source.getProofOfBusiness())
				&& Objects.nonNull(source.getProofOfBusinessDocuments());
	}

}
