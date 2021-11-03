package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.documents.files.hyperwallet.HyperwalletSellerExtractService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.seller.impl.KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of {@link HyperwalletSellerExtractService}
 */
@Profile({ "!qa" })
@Slf4j
@Getter
@Service
public class HyperwalletSellerExtractServiceImpl implements HyperwalletSellerExtractService {

	private final HyperwalletSDKService hyperwalletSDKService;

	private final MailNotificationUtil kycMailNotificationUtil;

	private final KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor kycDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor;

	public HyperwalletSellerExtractServiceImpl(
			final KYCDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor kycDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil kycMailNotificationUtil) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
		this.kycDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor = kycDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<KYCDocumentSellerInfoModel> pushProofOfIdentityAndBusinessSellerDocuments(
			final List<KYCDocumentSellerInfoModel> kycDocumentSellerInfoModelList) {
		//@formatter:off
        printShopsToSkip(kycDocumentSellerInfoModelList);
        final Map<KYCDocumentSellerInfoModel, List<HyperwalletVerificationDocument>> kycDocumentInfoModelListMap = kycDocumentSellerInfoModelList
                .stream()
                .filter(KYCDocumentSellerInfoModel::areDocumentsFilled)
                .map(kycDocumentInfoModel -> Pair.of(kycDocumentInfoModel,
                        kycDocumentInfoToHWVerificationDocumentMultipleStrategyExecutor.execute(kycDocumentInfoModel)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        //@formatter:on

		//@formatter:off
        return kycDocumentInfoModelListMap.entrySet()
                .stream()
                .filter(kycDocumentInfoModelListEntry -> ObjectUtils.isNotEmpty(kycDocumentInfoModelListEntry.getValue()))
                .map(this::callHyperwalletAPI)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //@formatter:on
	}

	/**
	 * {@inheritDoc}
	 */
	protected KYCDocumentSellerInfoModel callHyperwalletAPI(
			final Map.Entry<KYCDocumentSellerInfoModel, List<HyperwalletVerificationDocument>> entry) {
		final KYCDocumentSellerInfoModel kycDocumentSellerInfoModel = entry.getKey();
		final String documentsToUpload = entry.getValue().stream()
				.map(hyperwalletVerificationDocument -> hyperwalletVerificationDocument.getUploadFiles().keySet())
				.flatMap(Collection::stream).collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR));
		try {

			final Hyperwallet hyperwallet = hyperwalletSDKService
					.getHyperwalletInstance(kycDocumentSellerInfoModel.getHyperwalletProgram());
			hyperwallet.uploadUserDocuments(kycDocumentSellerInfoModel.getUserToken(), entry.getValue());
			log.info("Documents [{}] uploaded for shop with id [{}]", documentsToUpload,
					kycDocumentSellerInfoModel.getClientUserId());
			return kycDocumentSellerInfoModel;
		}
		catch (final HyperwalletException e) {
			log.error("Error uploading document to hyperwallet: [{}]", HyperwalletLoggingErrorsUtil.stringify(e));
			kycMailNotificationUtil.sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
					String.format("Something went wrong pushing documents to Hyperwallet for shop Id [%s]%n%s",
							kycDocumentSellerInfoModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
			return null;
		}
	}

	private void printShopsToSkip(final List<KYCDocumentSellerInfoModel> kycDocumentInfoModelCollection) {
//@formatter:off
        final List<KYCDocumentSellerInfoModel> shopsToSkip = kycDocumentInfoModelCollection.stream()
                .filter(Predicate.not(KYCDocumentSellerInfoModel::areDocumentsFilled))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(shopsToSkip)) {
            log.warn("Mandatory documents missing for shop with id [{}] ",
                    shopsToSkip.stream()
                            .map(KYCDocumentInfoModel::getClientUserId)
                            .collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
        }
        //@formatter:on
	}

}
