package com.paypal.kyc.service.documents.files.mirakl.impl;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShopReturn;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdatedShops;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentSellerInfoModel;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentDownloadExtractService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

	private final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	private final MailNotificationUtil kycMailNotificationUtil;

	public MiraklSellerDocumentsExtractServiceImpl(
			final MiraklSellerDocumentDownloadExtractService miraklSellerDocumentDownloadExtractService,
			final Converter<Date, MiraklGetShopsRequest> miraklGetShopsRequestConverter,
			final Converter<MiraklShop, KYCDocumentSellerInfoModel> miraklShopKYCDocumentInfoModelConverter,
			final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
			final MailNotificationUtil kycMailNotificationUtil) {
		super(miraklOperatorClient);
		this.miraklSellerDocumentDownloadExtractService = miraklSellerDocumentDownloadExtractService;
		this.miraklGetShopsRequestConverter = miraklGetShopsRequestConverter;
		this.miraklShopKYCDocumentInfoModelConverter = miraklShopKYCDocumentInfoModelConverter;
		this.miraklOperatorClient = miraklOperatorClient;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<KYCDocumentSellerInfoModel> extractProofOfIdentityAndBusinessSellerDocuments(final Date delta) {
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
		final List<KYCDocumentSellerInfoModel> shopsWithVerificationRequired = kycDocumentInfoList.stream()
						.filter(KYCDocumentSellerInfoModel::isRequiresKYC)
						.collect(Collectors.toList());
		//@formatter:on

		if (!CollectionUtils.isEmpty(shopsWithVerificationRequired)) {
			//@formatter:off
			log.info("Shops with KYC seller verification required: [{}]",
							shopsWithVerificationRequired.stream()
											.map(KYCDocumentSellerInfoModel::getClientUserId)
											.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
			//@formatter:on
		}

		skipShopsWithNonSelectedDocuments(shopsWithVerificationRequired);

		//@formatter:off
		final List<KYCDocumentSellerInfoModel> shopsWithSelectedVerificationDocuments = shopsWithVerificationRequired.stream()
						.filter(KYCDocumentSellerInfoModel::hasSelectedDocumentControlFields)
						.collect(Collectors.toList());
		//@formatter:on

		//@formatter:off
		return shopsWithSelectedVerificationDocuments.stream()
						.filter(kycDocumentInfoModel -> !ObjectUtils.isEmpty(kycDocumentInfoModel.getUserToken()))
						.map(miraklSellerDocumentDownloadExtractService::getDocumentsSelectedBySeller)
						.collect(Collectors.toList());
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
	public Optional<MiraklUpdatedShops> setFlagToPushProofOfIdentityAndBusinessSellerDocumentsToFalse(
			final List<KYCDocumentSellerInfoModel> successfullyPushedListOfDocuments) {
		final List<String> shopIdToUpdateList = successfullyPushedListOfDocuments.stream()
				.map(KYCDocumentSellerInfoModel::getClientUserId).collect(Collectors.toList());

		return miraklUpdateShopCall(shopIdToUpdateList);
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
			final List<KYCDocumentSellerInfoModel> shopsWithVerificationRequired) {

		//@formatter:off
		final List<KYCDocumentSellerInfoModel> shopsWithNonSelectedVerificationDocuments = shopsWithVerificationRequired.stream()
						.filter(Predicate
										.not(KYCDocumentSellerInfoModel::hasSelectedDocumentControlFields))
						.collect(Collectors
										.toList());
		//@formatter:on

		if (!CollectionUtils.isEmpty(shopsWithNonSelectedVerificationDocuments)) {
			log.warn("Skipping shops for seller with non selected documents to push to hyperwallet: [{}]",
					shopsWithNonSelectedVerificationDocuments.stream().map(KYCDocumentSellerInfoModel::getClientUserId)
							.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
		}
	}

	private Optional<MiraklUpdatedShops> miraklUpdateShopCall(final List<String> shopIdList) {
		if (CollectionUtils.isEmpty(shopIdList)) {
			return Optional.empty();
		}

		final List<MiraklUpdateShop> shopsToUpdate = shopIdList.stream().map(Long::valueOf).map(shopId -> {
			final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
			miraklUpdateShop.setShopId(shopId);
			miraklUpdateShop.setAdditionalFieldValues(
					List.of(new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
							KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD,
							Boolean.FALSE.toString().toLowerCase())));
			return miraklUpdateShop;
		}).collect(Collectors.toList());

		final MiraklUpdateShopsRequest miraklUpdateShopRequest = new MiraklUpdateShopsRequest(shopsToUpdate);
		try {
			final MiraklUpdatedShops miraklUpdatedShops = miraklOperatorClient.updateShops(miraklUpdateShopRequest);
			//@formatter:on
			log.info("Setting required KYC flag for shops with ids [{}] to false",
					miraklUpdatedShops.getShopReturns().stream().map(MiraklUpdatedShopReturn::getShopUpdated)
							.map(MiraklShop::getId)
							.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));
			return Optional.of(miraklUpdatedShops);
			//@formatter:off

		} catch (final MiraklException e) {
			log.error("Something went wrong when removing flag to retrieve documents for shops [{}]", String.join(",", shopIdList));
			kycMailNotificationUtil.sendPlainTextEmail("Issue setting push document flags to false in Mirakl",
							String.format("Something went wrong setting push document flag to false in Mirakl for shop Id [%s]%n%s",
											String.join(",", shopIdList),
											MiraklLoggingErrorsUtil.stringify(e)));
		}

		return Optional.empty();
	}

}
