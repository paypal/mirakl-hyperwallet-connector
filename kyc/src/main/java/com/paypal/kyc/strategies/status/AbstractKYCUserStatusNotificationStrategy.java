package com.paypal.kyc.strategies.status;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.MiraklShopKyc;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.converter.Converter;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD;
import static com.paypal.kyc.model.KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD;

@Slf4j
public abstract class AbstractKYCUserStatusNotificationStrategy
		implements Strategy<KYCUserStatusNotificationBodyModel, Optional<Void>> {

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	public static final String COMMA = ",";

	protected final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	protected final MailNotificationUtil mailNotificationUtil;

	protected final KYCRejectionReasonService kycRejectionReasonService;

	protected MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService;

	protected Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverter;

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
	public Optional<Void> execute(final KYCUserStatusNotificationBodyModel kycUserNotification) {
		return updateShop(kycUserNotification);
	}

	protected Optional<Void> updateShop(final KYCUserStatusNotificationBodyModel kycUserStatusNotificationBodyModel) {
		final MiraklShopKycStatus status = expectedKycMiraklStatus(kycUserStatusNotificationBodyModel);
		if (Objects.nonNull(status)) {
			final String shopId = kycUserStatusNotificationBodyModel.getClientUserId();
			final MiraklUpdateShopsRequest request = createUpdateShopRequest(kycUserStatusNotificationBodyModel,
					status);
			log.debug("Mirakl update shop request: [{}]", ToStringBuilder.reflectionToString(request));
			log.info("Updating KYC status for shop [{}]", shopId);
			try {
				final MiraklUpdatedShops response = miraklOperatorClient.updateShops(request);
				Optional.ofNullable(response)
						.ifPresent(miraklUpdatedShopsResponse -> log.debug("Mirakl update shop response: [{}]",
								ToStringBuilder.reflectionToString(miraklUpdatedShopsResponse)));
				log.info("KYC status updated to [{}] for shop [{}]", status.name(), shopId);

				deleteInvalidDocuments(kycUserStatusNotificationBodyModel);
			}
			catch (final MiraklException ex) {
				log.error("Something went wrong updating information of shop [{}]", shopId);
				mailNotificationUtil.sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
						String.format(
								ERROR_MESSAGE_PREFIX + "Something went wrong updating KYC information of shop [%s]%n%s",
								shopId, MiraklLoggingErrorsUtil.stringify(ex)));
			}
		}

		return Optional.empty();
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
		final LocalDateTime miraklDateUplodaded = getLocalDateTimeFromDate(miraklShopDocument.getDateUploaded());
		return miraklDateUplodaded.isAfter(hyperwalletDateUploaded);
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

		final List<MiraklRequestAdditionalFieldValue> additionalFieldValues = new ArrayList<>();
		if (HyperwalletUser.VerificationStatus.REQUIRED
				.equals(kycUserStatusNotificationBodyModel.getVerificationStatus())) {
			final var kycVerificationStatusCustomField = new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue();
			kycVerificationStatusCustomField.setCode(HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD);
			kycVerificationStatusCustomField.setValue(Boolean.TRUE.toString());
			additionalFieldValues.add(kycVerificationStatusCustomField);
		}
		if (HyperwalletUser.LetterOfAuthorizationStatus.REQUIRED
				.equals(kycUserStatusNotificationBodyModel.getLetterOfAuthorizationStatus())) {
			final var kycLetterOfAuthorizationStatusCustomField = new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue();
			kycLetterOfAuthorizationStatusCustomField
					.setCode(HYPERWALLET_KYC_REQUIRED_PROOF_AUTHORIZATION_BUSINESS_FIELD);
			kycLetterOfAuthorizationStatusCustomField.setValue(Boolean.TRUE.toString());
			additionalFieldValues.add(kycLetterOfAuthorizationStatusCustomField);
		}
		if (!CollectionUtils.isEmpty(additionalFieldValues)) {
			miraklUpdateShop.setAdditionalFieldValues(additionalFieldValues);
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

}
