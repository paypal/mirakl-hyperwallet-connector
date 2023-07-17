package com.paypal.kyc.documentextractioncommons.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.logging.LoggingConstantsUtil;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.stakeholdersdocumentextraction.model.KYCDocumentBusinessStakeHolderInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
public class HyperwalletDocumentUploadServiceImpl implements HyperwalletDocumentUploadService {

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	private final MailNotificationUtil kycMailNotificationUtil;

	public HyperwalletDocumentUploadServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uploadDocument(final KYCDocumentInfoModel kycDocumentInfoModel,
			final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments) {
		try {
			final Hyperwallet hyperwallet = userHyperwalletSDKService
					.getHyperwalletInstanceByHyperwalletProgram(kycDocumentInfoModel.getHyperwalletProgram());

			invokeHyperwalletAPI(kycDocumentInfoModel, hyperwalletVerificationDocuments, hyperwallet);

			logUploadedDocuments(kycDocumentInfoModel, hyperwalletVerificationDocuments);
		}
		catch (final HyperwalletException e) {
			log.error(
					"Error uploading document to Hyperwallet.%n%s".formatted(HyperwalletLoggingErrorsUtil.stringify(e)),
					e);

			reportError(kycDocumentInfoModel, e);

			throw new HMCHyperwalletAPIException(e);
		}
	}

	protected void invokeHyperwalletAPI(final KYCDocumentInfoModel kycDocumentInfoModel,
			final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments,
			final Hyperwallet hyperwallet) {
		if (kycDocumentInfoModel instanceof KYCDocumentBusinessStakeHolderInfoModel) {
			final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = (KYCDocumentBusinessStakeHolderInfoModel) kycDocumentInfoModel;
			hyperwallet.uploadStakeholderDocuments(kycDocumentBusinessStakeHolderInfoModel.getUserToken(),
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
				"Something went wrong pushing documents to Hyperwallet for %s%n%s".formatted(
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
