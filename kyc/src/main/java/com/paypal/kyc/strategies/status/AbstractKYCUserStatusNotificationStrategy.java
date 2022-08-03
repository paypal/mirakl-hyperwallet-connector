package com.paypal.kyc.strategies.status;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.domain.common.error.ErrorBean;
import com.mirakl.client.mmp.domain.shop.MiraklShopKyc;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShopWithErrors;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShopReturn;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentNotificationModel;
import com.paypal.kyc.model.KYCDocumentStatusEnum;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCRejectionReasonService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD;
import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD;

@Slf4j
public abstract class AbstractKYCUserStatusNotificationStrategy
		implements Strategy<KYCUserStatusNotificationBodyModel, Void> {

	protected static final String COMMA = ",";

	protected static final String MAIL_SUBJECT = "Issue detected updating KYC information in Mirakl";

	protected static final String MSG_ERROR_DETECTED = "Something went wrong updating KYC information of shop [%s]%n%s";

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	@Value("${hyperwallet.kycAutomated}")
	protected boolean isKycAutomated;

	protected final MailNotificationUtil mailNotificationUtil;

	protected final KYCRejectionReasonService kycRejectionReasonService;

	protected final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	protected final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	protected final Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverter;

	protected AbstractKYCUserStatusNotificationStrategy(
			final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
			final MailNotificationUtil mailNotificationUtil, final KYCRejectionReasonService kycRejectionReasonService,
			final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverter) {
		this.miraklOperatorClient = miraklOperatorClient;
		this.mailNotificationUtil = mailNotificationUtil;
		this.kycRejectionReasonService = kycRejectionReasonService;
		this.miraklSellerDocumentsExtractService = miraklSellerDocumentsExtractService;
		this.kycDocumentNotificationModelListConverter = kycDocumentNotificationModelListConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void execute(final KYCUserStatusNotificationBodyModel kycUserNotification) {
		updateShop(kycUserNotification);
		return null;
	}

	protected void updateShop(final KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModel) {
		final MiraklShopKycStatus status = expectedKycMiraklStatus(kycUserStatusNotificationBodyModel);
		if (Objects.nonNull(status)) {
			final String shopId = kycUserStatusNotificationBodyModel.getClientUserId();
			final MiraklUpdateShopsRequest request = createUpdateShopRequest(kycUserStatusNotificationBodyModel,
					status);
			log.info("Updating KYC status for shop [{}]", shopId);
			try {
				final MiraklUpdatedShops response = miraklOperatorClient.updateShops(request);
				if (response == null) {
					log.error("No response was received for update request for shop [{}]", shopId);
				}
				else {
					final List<MiraklUpdatedShopReturn> shopReturns = response.getShopReturns();
					shopReturns.forEach(this::logShopUpdates);
				}

				deleteInvalidDocuments(kycUserStatusNotificationBodyModel);
			}
			catch (final MiraklException ex) {
				final String errorMessage = String.format(MSG_ERROR_DETECTED, shopId,
						MiraklLoggingErrorsUtil.stringify(ex));
				log.error(errorMessage, ex);
				mailNotificationUtil.sendPlainTextEmail(MAIL_SUBJECT, ERROR_MESSAGE_PREFIX + errorMessage);
				// Rethrow exception to handle it in AbstractNotificationListener
				throw ex;
			}
		}
	}

	protected void deleteInvalidDocuments(final KYCUserStatusNotificationBodyModel kycUserNotification) {
		final String clientUserId = kycUserNotification.getClientUserId();

		final KYCDocumentInfoModel kycDocumentInfoModel = miraklSellerDocumentsExtractService
				.extractKYCSellerDocuments(clientUserId);

		final Map<String, LocalDateTime> documentsToBeDeleted = kycUserNotification.getDocuments().stream()
				.filter(kycDocumentNotificationModel -> KYCDocumentStatusEnum.INVALID
						.equals(kycDocumentNotificationModel.getDocumentStatus()))
				.map(kycDocumentNotificationModel -> Pair.of(
						kycDocumentNotificationModelListConverter.convert(kycDocumentNotificationModel),
						kycDocumentNotificationModel.getCreatedOn()))
				.map(this::getMapDocumentUploadTime).flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (MapUtils.isNotEmpty(documentsToBeDeleted)) {
			final List<MiraklShopDocument> miraklDocumentsToBeDeleted = kycDocumentInfoModel.getMiraklShopDocuments()
					.stream()
					.filter(miraklShopDocument -> documentsToBeDeleted.containsKey(miraklShopDocument.getTypeCode()))
					.filter(Predicate
							.not(miraklShopDocument -> isANewMiraklDocument(documentsToBeDeleted, miraklShopDocument)))
					.collect(Collectors.toList());

			final String documentTypeCodesToBeDeleted = miraklDocumentsToBeDeleted.stream()
					.map(MiraklShopDocument::getTypeCode).collect(Collectors.joining(COMMA));

			if (!StringUtils.isEmpty(documentTypeCodesToBeDeleted)) {
				log.info("Deleting documents [{}] for shop [{}]", documentTypeCodesToBeDeleted, clientUserId);
				miraklSellerDocumentsExtractService.deleteDocuments(miraklDocumentsToBeDeleted);
				log.info("Documents deleted");
			}
		}
	}

	private boolean isANewMiraklDocument(final Map<String, LocalDateTime> documentsToBeDeleted,
			final MiraklShopDocument miraklShopDocument) {
		final LocalDateTime hyperwalletDateUploaded = documentsToBeDeleted.get(miraklShopDocument.getTypeCode());
		final LocalDateTime miraklDateUploaded = getLocalDateTimeFromDate(miraklShopDocument.getDateUploaded());
		return miraklDateUploaded.isAfter(hyperwalletDateUploaded);
	}

	protected abstract MiraklShopKycStatus expectedKycMiraklStatus(
			final KYCUserStatusNotificationBodyModel incomingNotification);

	private MiraklUpdateShopsRequest createUpdateShopRequest(
			final KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModel,
			final MiraklShopKycStatus status) {

		final String shopId = kycUserStatusNotificationBodyModel.getClientUserId();
		final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
		miraklUpdateShop.setShopId(Long.valueOf(shopId));

		//@formatter:off
		miraklUpdateShop.setKyc(new MiraklShopKyc(status, kycRejectionReasonService.getRejectionReasonDescriptions(kycUserStatusNotificationBodyModel.getReasonsType())));
		//@formatter:on

		if (isKycAutomated()) {
			final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = new ArrayList<>();
			if (HyperwalletUser.VerificationStatus.REQUIRED
					.equals(kycUserStatusNotificationBodyModel.getVerificationStatus())) {
				final MiraklSimpleRequestAdditionalFieldValue kycVerificationStatusCustomField = new MiraklSimpleRequestAdditionalFieldValue();
				kycVerificationStatusCustomField.setCode(HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
				kycVerificationStatusCustomField.setValue(Boolean.TRUE.toString());
				additionalFieldValues.add(kycVerificationStatusCustomField);
			}
			if (HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED
					.equals(kycUserStatusNotificationBodyModel.getLetterOfAuthorizationStatus())) {
				final MiraklSimpleRequestAdditionalFieldValue kycLetterOfAuthorizationStatusCustomField = new MiraklSimpleRequestAdditionalFieldValue();
				kycLetterOfAuthorizationStatusCustomField
						.setCode(HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD);
				kycLetterOfAuthorizationStatusCustomField.setValue(Boolean.TRUE.toString());
				additionalFieldValues.add(kycLetterOfAuthorizationStatusCustomField);
			}
			if (!CollectionUtils.isEmpty(additionalFieldValues)) {
				miraklUpdateShop.setAdditionalFieldValues(additionalFieldValues);
			}
		}
		return new MiraklUpdateShopsRequest(List.of(miraklUpdateShop));
	}

	private Map<String, LocalDateTime> getMapDocumentUploadTime(final Pair<List<String>, LocalDateTime> documents) {
		return documents.getLeft().stream().map(documentType -> Pair.of(documentType, documents.getRight()))
				.collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
	}

	private LocalDateTime getLocalDateTimeFromDate(final Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	private void logShopUpdates(final MiraklUpdatedShopReturn updatedShopReturn) {
		Optional.ofNullable(updatedShopReturn.getShopUpdated()).ifPresent(
				shop -> log.info("KYC status updated to [{}] for shop [{}]", shop.getKyc().getStatus(), shop.getId()));

		Optional.ofNullable(updatedShopReturn.getShopError()).ifPresent(this::logErrorMessage);
	}

	private void logErrorMessage(final MiraklUpdateShopWithErrors shopError) {
		final Long shopId = shopError.getInput().getShopId();
		//@formatter:off
		final String miraklUpdateErrors = shopError.getErrors().stream()
				.map(ErrorBean::toString)
				.collect(Collectors.joining(","));
		//@formatter:on

		final String errorMessage = String.format(MSG_ERROR_DETECTED, shopId, miraklUpdateErrors);

		log.error(errorMessage);
		mailNotificationUtil.sendPlainTextEmail(MAIL_SUBJECT, ERROR_MESSAGE_PREFIX + errorMessage);
		throw new HMCException(errorMessage);
	}

	protected boolean isKycAutomated() {
		return isKycAutomated;
	}

}
