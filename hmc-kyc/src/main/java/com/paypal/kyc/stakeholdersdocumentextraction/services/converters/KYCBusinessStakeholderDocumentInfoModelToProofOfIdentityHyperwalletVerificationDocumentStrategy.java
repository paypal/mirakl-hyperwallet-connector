package com.paypal.kyc.stakeholdersdocumentextraction.services.converters;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentCategoryEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KYCBusinessStakeholderDocumentInfoModelToProofOfIdentityHyperwalletVerificationDocumentStrategy
		implements Strategy<KYCDocumentBusinessStakeHolderInfoModel, HyperwalletVerificationDocument> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletVerificationDocument execute(final KYCDocumentBusinessStakeHolderInfoModel source) {
		final String countryIsoCode = source.getCountryIsoCode();

		//@formatter:off
		final Map<String, String> uploadFiles = source.getIdentityDocuments()
				.stream()
				.collect(Collectors.toMap(kycDocumentModel -> source.getProofOfIdentity()
						.name()
						.toLowerCase() + '_' + kycDocumentModel
						.getDocumentSide()
						.name()
						.toLowerCase(), kycDocumentModel -> kycDocumentModel.getFile()
						.getAbsolutePath()));
		//@formatter:on

		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setType(source.getProofOfIdentity().name());
		hyperwalletVerificationDocument.setCountry(countryIsoCode);
		hyperwalletVerificationDocument.setCategory(KYCDocumentCategoryEnum.IDENTIFICATION.name());
		hyperwalletVerificationDocument.setUploadFiles(uploadFiles);

		return hyperwalletVerificationDocument;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCDocumentBusinessStakeHolderInfoModel source) {
		return Objects.nonNull(source.getProofOfIdentity()) && !ObjectUtils.isEmpty(source.getIdentityDocuments());
	}

}
