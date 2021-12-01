package com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentCategoryEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KYCBusinessStakeholderDocumentInfoModelToLetterOfAuthorizationHyperwalletVerificationDocumentStrategy
		implements Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument> {

	private static final String LETTER_OF_AUTHORIZATION_NAME = "letter_of_authorization_front";

	private static final String LETTER_OF_AUTHORIZATION_TYPE = "LETTER_OF_AUTHORIZATION";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletVerificationDocument execute(final KYCDocumentBusinessStakeHolderInfoModel source) {
		//@formatter:off
		final Map<String, String> uploadFiles = source.getLetterOfAuthorizationDocument()
				.stream()
				.collect(Collectors.toMap(kycDocumentModel -> LETTER_OF_AUTHORIZATION_NAME, kycDocumentModel -> kycDocumentModel.getFile()
						.getAbsolutePath()));
		//@formatter:on

		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setType(LETTER_OF_AUTHORIZATION_TYPE);
		hyperwalletVerificationDocument.setCategory(KYCDocumentCategoryEnum.AUTHORIZATION.name());
		hyperwalletVerificationDocument.setUploadFiles(uploadFiles);

		return hyperwalletVerificationDocument;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCDocumentBusinessStakeHolderInfoModel source) {
		return source.isRequiresLetterOfAuthorization() && source.isContact()
				&& !ObjectUtils.isEmpty(source.getLetterOfAuthorizationDocument());
	}

}
