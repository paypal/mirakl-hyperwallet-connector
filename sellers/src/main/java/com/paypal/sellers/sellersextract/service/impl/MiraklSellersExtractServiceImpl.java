package com.paypal.sellers.sellersextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.StrategyFactory;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.sellers.infrastructure.utils.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue;
import static com.paypal.sellers.sellersextract.model.SellerModelConstants.HYPERWALLET_USER_TOKEN;

/**
 * Class to handle operations related with the seller extraction from Mirakl
 */
@Primary
@Slf4j
@Service
public class MiraklSellersExtractServiceImpl implements MiraklSellersExtractService {

	private static final String SHOPS_RETRIEVED_MESSAGE = "Shops retrieved [{}]";

	private static final String EMAIL_SUBJECT_MESSAGE = "Issue detected getting shop information in Mirakl";

	private final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient;

	private final StrategyFactory<MiraklShop, SellerModel> miraklShopSellerModelStrategyFactory;

	private final MailNotificationUtil sellerMailNotificationUtil;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public MiraklSellersExtractServiceImpl(final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
			final StrategyFactory<MiraklShop, SellerModel> miraklShopSellerModelStrategyFactory,
			final MailNotificationUtil sellerMailNotificationUtil) {
		this.miraklOperatorClient = miraklOperatorClient;
		this.miraklShopSellerModelStrategyFactory = miraklShopSellerModelStrategyFactory;
		this.sellerMailNotificationUtil = sellerMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractIndividuals(@Nullable final Date delta) {
		final MiraklShops shops = retrieveMiraklShopsByShopIds(delta);
		return internalExtractIndividuals(shops);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractIndividuals(final List<String> shopIds) {
		if (CollectionUtils.isEmpty(shopIds)) {
			return Collections.emptyList();
		}
		final MiraklShops shops = retrieveMiraklShopsByShopIds(shopIds);
		return internalExtractIndividuals(shops);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractProfessionals(@Nullable final Date delta) {
		final MiraklShops shops = retrieveMiraklShopsByShopIds(delta);
		return internalExtractProfessionals(shops);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractProfessionals(final List<String> shopIds) {
		if (CollectionUtils.isEmpty(shopIds)) {
			return Collections.emptyList();
		}
		final MiraklShops shops = retrieveMiraklShopsByShopIds(shopIds);
		return internalExtractProfessionals(shops);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractSellers(final List<String> shopIds) {
		if (CollectionUtils.isEmpty(shopIds)) {
			return Collections.emptyList();
		}
		final MiraklShops shops = retrieveMiraklShopsByShopIds(shopIds);
		return Stream.ofNullable(shops.getShops()).flatMap(Collection::stream)
				.map(miraklShopSellerModelStrategyFactory::execute).collect(Collectors.toList());
	}

	@Override
	public List<SellerModel> extractSellers(@Nullable final Date delta) {
		final MiraklShops shops = retrieveMiraklShopsByShopIds(delta);
		//@formatter:off
		log.info(SHOPS_RETRIEVED_MESSAGE, Stream.ofNullable(shops.getShops())
				.flatMap(Collection::stream)
				.map(MiraklShop::getId)
				.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));


		return Stream.ofNullable(shops.getShops())
				.flatMap(Collection::stream)
				.map(miraklShopSellerModelStrategyFactory::execute)
				.filter(SellerModel::hasAcceptedTermsAndConditions)
				.collect(Collectors.toList());

		//@formatter:on
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUserToken(final HyperwalletUser hyperwalletUser) {
		final MiraklUpdateShop mirakUpdateShop = new MiraklUpdateShop();
		final String shopId = hyperwalletUser.getClientUserId();
		mirakUpdateShop.setShopId(Long.valueOf(shopId));
		final var userTokenCustomField = new MiraklSimpleRequestAdditionalFieldValue();
		userTokenCustomField.setCode(HYPERWALLET_USER_TOKEN);
		userTokenCustomField.setValue(hyperwalletUser.getToken());
		mirakUpdateShop.setAdditionalFieldValues(List.of(userTokenCustomField));
		final MiraklUpdateShopsRequest request = new MiraklUpdateShopsRequest(List.of(mirakUpdateShop));
		log.info("Updating token for shop [{}]", shopId);
		log.debug("Update shop request [{}]", ToStringBuilder.reflectionToString(request));
		try {
			final var miraklUpdatedShops = miraklOperatorClient.updateShops(request);
			Optional.ofNullable(miraklUpdatedShops).ifPresent(
					response -> log.debug("Update shop response [{}]", ToStringBuilder.reflectionToString(response)));
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting information of shop [{}]", shopId);
			sellerMailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					String.format(ERROR_MESSAGE_PREFIX + "Something went wrong getting information of shop [%s]%n%s",
							shopId, MiraklLoggingErrorsUtil.stringify(ex)));
		}
	}

	@NonNull
	private List<SellerModel> internalExtractIndividuals(final MiraklShops shops) {
		//@formatter:off
		log.info(SHOPS_RETRIEVED_MESSAGE, shops.getShops().stream()
				.filter(Predicate.not(MiraklShop::isProfessional))
				.map(MiraklShop::getId)
				.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));

		return shops.getShops()
				.stream()
				.filter(Predicate.not(MiraklShop::isProfessional))
				.map(miraklShopSellerModelStrategyFactory::execute)
				.filter(SellerModel::hasAcceptedTermsAndConditions)
				.collect(Collectors.toList());
		//@formatter:on
	}

	@NonNull
	private List<SellerModel> internalExtractProfessionals(final MiraklShops shops) {
		//@formatter:off
		log.info(SHOPS_RETRIEVED_MESSAGE, shops.getShops().stream()
				.filter(MiraklShop::isProfessional)
				.map(MiraklShop::getId)
				.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));

		return shops.getShops()
				.stream()
				.filter(MiraklShop::isProfessional)
				.map(miraklShopSellerModelStrategyFactory::execute)
				.filter(SellerModel::hasAcceptedTermsAndConditions)
				.collect(Collectors.toList());
		//@formatter:on
	}

	private MiraklShops retrieveMiraklShopsByShopIds(@Nullable final Date delta) {
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		request.setUpdatedSince(delta);
		request.setPaginate(false);
		log.info("Retrieving shops since {}", delta);
		log.debug("Get Shops request [{}]", ToStringBuilder.reflectionToString(request));
		try {
			final var shops = miraklOperatorClient.getShops(request);
			Optional.ofNullable(shops).ifPresent(shopResponse -> log.debug("Get Shops response [{}]",
					ToStringBuilder.reflectionToString(shopResponse)));

			return shops;
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting shop information since [{}]", delta);
			sellerMailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					String.format(ERROR_MESSAGE_PREFIX + "Something went wrong getting shop information since [%s]%n%s",
							delta, MiraklLoggingErrorsUtil.stringify(ex)));
			return new MiraklShops();
		}
	}

	private MiraklShops retrieveMiraklShopsByShopIds(final List<String> shopIds) {
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		request.setShopIds(shopIds);
		request.setPaginate(false);
		log.info("Retrieving shops with ids {}", shopIds);
		log.debug("Get Shops request [{}]", ToStringBuilder.reflectionToString(request));
		try {
			final var shops = miraklOperatorClient.getShops(request);
			Optional.ofNullable(shops).ifPresent(shopResponse -> log.debug("Get Shops response [{}]",
					ToStringBuilder.reflectionToString(shopResponse)));

			return shops;
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting s information with ids [{}]", shopIds);
			sellerMailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					String.format(
							ERROR_MESSAGE_PREFIX + "Something went wrong getting shop information with ids [%s]%n%s",
							shopIds, MiraklLoggingErrorsUtil.stringify(ex)));
			return new MiraklShops();
		}
	}

}
