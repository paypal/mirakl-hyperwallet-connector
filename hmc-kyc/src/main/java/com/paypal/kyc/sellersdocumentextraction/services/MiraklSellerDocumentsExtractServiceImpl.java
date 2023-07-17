package com.paypal.kyc.sellersdocumentextraction.services;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShopReturn;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.infrastructure.support.logging.LoggingConstantsUtil;
import com.paypal.kyc.documentextractioncommons.support.AbstractMiraklDocumentExtractServiceImpl;
import com.paypal.kyc.documentextractioncommons.model.KYCConstants;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import com.paypal.kyc.sellersdocumentextraction.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentsExtractionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link MiraklSellerDocumentsExtractService}
 */
@Slf4j
@Service
public class MiraklSellerDocumentsExtractServiceImpl extends AbstractMiraklDocumentExtractServiceImpl
		implements MiraklSellerDocumentsExtractService {

	private final MiraklSellerDocumentDownloadExtractService miraklSellerDocumentDownloadExtractService;

	private final Converter<Date, MiraklGetShopsRequest> miraklGetShopsRequestConverter;

	private final Converter<MiraklShop, KYCDocumentSellerInfoModel> miraklShopKYCDocumentInfoModelConverter;

	private final MiraklClient miraklOperatorClient;

	public MiraklSellerDocumentsExtractServiceImpl(
			final MiraklSellerDocumentDownloadExtractService miraklSellerDocumentDownloadExtractService,
			final Converter<Date, MiraklGetShopsRequest> miraklGetShopsRequestConverter,
			final Converter<MiraklShop, KYCDocumentSellerInfoModel> miraklShopKYCDocumentInfoModelConverter,
			final MiraklClient miraklOperatorClient, final MailNotificationUtil kycMailNotificationUtil) {
		super(miraklOperatorClient, kycMailNotificationUtil);
		this.miraklSellerDocumentDownloadExtractService = miraklSellerDocumentDownloadExtractService;
		this.miraklGetShopsRequestConverter = miraklGetShopsRequestConverter;
		this.miraklShopKYCDocumentInfoModelConverter = miraklShopKYCDocumentInfoModelConverter;
		this.miraklOperatorClient = miraklOperatorClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KYCDocumentsExtractionResult<KYCDocumentSellerInfoModel> extractProofOfIdentityAndBusinessSellerDocuments(
			final Date delta) {
		final MiraklGetShopsRequest miraklGetShopsRequest = miraklGetShopsRequestConverter.convert(delta);
		log.info("Retrieving modified shops for proof of identity/business sellers documents since [{}]", delta);
		final MiraklShops shops = miraklOperatorClient.getShops(miraklGetShopsRequest);

		//@formatter:off
		log.info("Retrieved modified shops since [{}]: [{}]", delta,
				Stream.ofNullable(shops.getShops())
						.flatMap(Collection::stream)
						.map(MiraklShop::getId)
						.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
		//@formatter:on

		//@formatter:off
		final List<KYCDocumentSellerInfoModel> kycDocumentInfoList = Stream.ofNullable(shops.getShops())
				.flatMap(Collection::stream)
				.map(miraklShopKYCDocumentInfoModelConverter::convert)
				.collect(Collectors.toList());
		//@formatter:on

		//@formatter:off
		final Map<Boolean, List<KYCDocumentSellerInfoModel>> documentGroups = kycDocumentInfoList.stream()
				.collect(Collectors.groupingBy(KYCDocumentSellerInfoModel::isRequiresKYC));
		//@formatter:on

		final Collection<KYCDocumentSellerInfoModel> docsFromShopsWithVerificationRequired = CollectionUtils
				.emptyIfNull(documentGroups.get(true));
		final Collection<KYCDocumentSellerInfoModel> docsFromShopsWithoutVerificationRequired = CollectionUtils
				.emptyIfNull(documentGroups.get(false));

		if (!CollectionUtils.isEmpty(docsFromShopsWithVerificationRequired)) {
			//@formatter:off
			log.info("Shops with KYC seller verification required: [{}]",
					docsFromShopsWithVerificationRequired.stream()
							.map(KYCDocumentSellerInfoModel::getClientUserId)
							.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
			//@formatter:on
		}

		if (!CollectionUtils.isEmpty(docsFromShopsWithoutVerificationRequired)) {
			//@formatter:off
			log.info("Shops without KYC seller verification required: [{}]",
					docsFromShopsWithoutVerificationRequired.stream()
							.map(KYCDocumentSellerInfoModel::getClientUserId)
							.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
			//@formatter:on
		}

		skipShopsWithNonSelectedDocuments(docsFromShopsWithVerificationRequired);

		//@formatter:off
		final List<KYCDocumentSellerInfoModel> shopsWithSelectedVerificationDocuments = docsFromShopsWithVerificationRequired.stream()
				.filter(KYCDocumentSellerInfoModel::hasSelectedDocumentControlFields)
				.collect(Collectors.toList());
		//@formatter:on

		final KYCDocumentsExtractionResult<KYCDocumentSellerInfoModel> kycDocumentsExtractionResult = new KYCDocumentsExtractionResult<>();
		//@formatter:off
		shopsWithSelectedVerificationDocuments.stream()
				.filter(kycDocumentInfoModel -> !ObjectUtils.isEmpty(kycDocumentInfoModel.getUserToken()))
				.forEach(kycDocumentSellerInfoModel -> kycDocumentsExtractionResult.addDocument(
						() -> miraklSellerDocumentDownloadExtractService.getDocumentsSelectedBySeller(kycDocumentSellerInfoModel)));

		return kycDocumentsExtractionResult;
		//@formatter:on
	}

	protected Optional<MiraklShop> extractMiraklShop(final String shopId) {
		final MiraklGetShopsRequest miraklGetShopsRequest = new MiraklGetShopsRequest();
		miraklGetShopsRequest.setShopIds(List.of(shopId));
		log.info("Retrieving shopId [{}]", shopId);
		final MiraklShops shops = miraklOperatorClient.getShops(miraklGetShopsRequest);

		return Optional.ofNullable(shops).orElse(new MiraklShops()).getShops().stream()
				.filter(shop -> shopId.equals(shop.getId())).findAny();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(
			final KYCDocumentSellerInfoModel successfullyPushedListOfDocument) {

		miraklUpdateShopCall(successfullyPushedListOfDocument.getClientUserId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KYCDocumentInfoModel extractKYCSellerDocuments(final String shopId) {
		return extractMiraklShop(shopId)
				.map(miraklShop -> miraklSellerDocumentDownloadExtractService
						.populateMiraklShopDocuments(miraklShopKYCDocumentInfoModelConverter.convert(miraklShop)))
				.orElse(null);
	}

	private void skipShopsWithNonSelectedDocuments(
			final Collection<KYCDocumentSellerInfoModel> shopsWithVerificationRequired) {

		//@formatter:off
		final List<KYCDocumentSellerInfoModel> shopsWithNonSelectedVerificationDocuments = shopsWithVerificationRequired.stream()
				.filter(Predicate.not(KYCDocumentSellerInfoModel::hasSelectedDocumentControlFields))
				.collect(Collectors.toList());
		//@formatter:on

		if (!CollectionUtils.isEmpty(shopsWithNonSelectedVerificationDocuments)) {
			log.warn("Skipping shops for seller with non selected documents to push to hyperwallet: [{}]",
					shopsWithNonSelectedVerificationDocuments.stream().map(KYCDocumentSellerInfoModel::getClientUserId)
							.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
		}
	}

	private void miraklUpdateShopCall(final String shopId) {

		final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
		miraklUpdateShop.setShopId(Long.valueOf(shopId));
		miraklUpdateShop.setAdditionalFieldValues(
				List.of(new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
						KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD,
						Boolean.FALSE.toString().toLowerCase())));

		final MiraklUpdateShopsRequest miraklUpdateShopRequest = new MiraklUpdateShopsRequest(
				List.of(miraklUpdateShop));

		try {
			final MiraklUpdatedShops miraklUpdatedShops = miraklOperatorClient.updateShops(miraklUpdateShopRequest);

			logUpdatedShops(miraklUpdatedShops);

		}
		catch (final MiraklException e) {

			log.error("Something went wrong when removing flag to retrieve documents for shop [%s]".formatted(shopId),
					e);

			reportError("Shop Id " + shopId, e);

			throw new HMCMiraklAPIException(e);
		}
	}

	private void logUpdatedShops(final MiraklUpdatedShops miraklUpdatedShops) {
		//@formatter:on
		log.info("Setting required KYC flag for shops with ids [{}] to false",
				miraklUpdatedShops.getShopReturns().stream().map(MiraklUpdatedShopReturn::getShopUpdated)
						.map(MiraklShop::getId)
						.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
		//@formatter:off
	}

}
