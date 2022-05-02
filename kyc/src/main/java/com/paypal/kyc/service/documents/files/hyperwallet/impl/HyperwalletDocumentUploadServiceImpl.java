package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
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

	private final HyperwalletSDKService hyperwalletSDKService;

	private final MailNotificationUtil kycMailNotificationUtil;

	public HyperwalletDocumentUploadServiceImpl(final HyperwalletSDKService hyperwalletSDKService,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uploadDocument(KYCDocumentInfoModel kycDocumentInfoModel,
			List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments) {
		try {
			final Hyperwallet hyperwallet = hyperwalletSDKService
					.getHyperwalletInstance(kycDocumentInfoModel.getHyperwalletProgram());

			invokeHyperwalletAPI(kycDocumentInfoModel, hyperwalletVerificationDocuments, hyperwallet);

			logUploadedDocuments(kycDocumentInfoModel, hyperwalletVerificationDocuments);
		}
		catch (final HyperwalletException e) {
			log.error("Error uploading document to Hyperwallet: [{}]", HyperwalletLoggingErrorsUtil.stringify(e));

			reportError(kycDocumentInfoModel, e);

			throw new HMCHyperwalletAPIException(e);
		}
	}

	protected void invokeHyperwalletAPI(KYCDocumentInfoModel kycDocumentInfoModel,
			List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments, Hyperwallet hyperwallet) {
		if (kycDocumentInfoModel instanceof KYCDocumentBusinessStakeHolderInfoModel) {
			KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) kycDocumentInfoModel;
			hyperwallet.uploadStakeholderDocuments(kycDocumentInfoModel.getUserToken(),
					kycDocumentBusinessStakeHolderInfoModel.getToken(), hyperwalletVerificationDocuments);
		}
		else {
			KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = (KYCDocumentSellerInfoModel) kycDocumentInfoModel;
			hyperwallet.uploadUserDocuments(kycDocumentSellerInfoModel.getUserToken(),
					hyperwalletVerificationDocuments);
		}
	}

	private void reportError(KYCDocumentInfoModel kycDocumentInfoModel, HyperwalletException e) {
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
