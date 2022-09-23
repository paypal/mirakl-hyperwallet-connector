package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletDocumentUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
@Profile({ "!qa" })
@Slf4j
@Service
public class HyperwalletDocumentUploadServiceImpl implements HyperwalletDocumentUploadService {

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	private final MailNotificationUtil kycMailNotificationUtil;

	public HyperwalletDocumentUploadServiceImpl(final HyperwalletSDKUserService hyperwalletSDKUserService,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uploadDocument(final KYCDocumentInfoModel kycDocumentInfoModel,
			final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments) {
		try {
			final Hyperwallet hyperwallet = hyperwalletSDKUserService
					.getHyperwalletInstanceByHyperwalletProgram(kycDocumentInfoModel.getHyperwalletProgram());

			invokeHyperwalletAPI(kycDocumentInfoModel, hyperwalletVerificationDocuments, hyperwallet);

			logUploadedDocuments(kycDocumentInfoModel, hyperwalletVerificationDocuments);
		}
		catch (final HyperwalletException e) {
			log.error(String.format("Error uploading document to Hyperwallet.%n%s",
					HyperwalletLoggingErrorsUtil.stringify(e)), e);

			reportError(kycDocumentInfoModel, e);

			throw new HMCHyperwalletAPIException(e);
		}
	}

	protected void invokeHyperwalletAPI(final KYCDocumentInfoModel kycDocumentInfoModel,
			final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments,
			final Hyperwallet hyperwallet) {
		if (kycDocumentInfoModel instanceof KYCDocumentBusinessStakeHolderInfoModel) {
			final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) kycDocumentInfoModel;
			hyperwallet.uploadStakeholderDocuments(kycDocumentInfoModel.getUserToken(),
					kycDocumentBusinessStakeHolderInfoModel.getToken(), hyperwalletVerificationDocuments);
		}
		else {
			final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) kycDocumentInfoModel;
			hyperwallet.uploadUserDocuments(kycDocumentSellerInfoModel.getUserToken(),
					hyperwalletVerificationDocuments);
		}
	}

	private void reportError(final KYCDocumentInfoModel kycDocumentInfoModel, final HyperwalletException e) {
		kycMailNotificationUtil.sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
				String.format("Something went wrong pushing documents to Hyperwallet for %s%n%s",
						kycDocumentInfoModel.getDocumentTracingIdentifier(),
						HyperwalletLoggingErrorsUtil.stringify(e)));
	}

	protected void logUploadedDocuments(final KYCDocumentInfoModel kycDocumentInfoModel,
			final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments) {

		final String documentsToUpload = hyperwalletVerificationDocuments.stream()
				.map(hyperwalletVerificationDocument -> hyperwalletVerificationDocument.getUploadFiles().keySet())
				.flatMap(Collection::stream).collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR));

		log.info("Documents [{}] uploaded for {}", documentsToUpload,
				kycDocumentInfoModel.getDocumentTracingIdentifier());
	}

}
