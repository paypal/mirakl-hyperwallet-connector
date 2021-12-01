package com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl;

import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.kyc.model.KYCDocumentCategoryEnum;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KYCDocumentInfoModelToProofOAddressHyperwalletVerificationDocumentStrategy
		implements Strategy<KYCDocumentSellerInfoModel, HyperwalletVerificationDocument> {

	@Override
	public HyperwalletVerificationDocument execute(final KYCDocumentSellerInfoModel source) {
		//@formatter:off
		final Map<String, String> uploadFiles = source.getAddressDocuments()
				.stream()
				.collect(Collectors.toMap(kycDocumentModel -> source.getProofOfAddress()
								.name()
								.toLowerCase()
								+ '_' + kycDocumentModel.getDocumentSide()
								.name()
								.toLowerCase(),
						kycDocumentModel -> kycDocumentModel.getFile()
								.getAbsolutePath()));
		//@formatter:on

		final HyperwalletVerificationDocument hyperwalletVerificationDocument = new HyperwalletVerificationDocument();
		hyperwalletVerificationDocument.setType(source.getProofOfAddress().name());
		hyperwalletVerificationDocument.setCategory(KYCDocumentCategoryEnum.ADDRESS.name());
		hyperwalletVerificationDocument.setUploadFiles(uploadFiles);

		return hyperwalletVerificationDocument;
	}

	@Override
	public boolean isApplicable(final KYCDocumentSellerInfoModel source) {
		return !source.isProfessional() && Objects.nonNull(source.getProofOfAddress())
				&& !ObjectUtils.isEmpty(source.getAddressDocuments());
	}

}
