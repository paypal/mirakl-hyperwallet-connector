package com.paypal.sellers.sellersextract.service.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.infrastructure.strategy.StrategyExecutor;
import com.paypal.infrastructure.util.LoggingConstantsUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

	private final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient;

	private final StrategyExecutor<MiraklShop, SellerModel> miraklShopSellerModelStrategyExecutor;

	private final MailNotificationUtil sellerMailNotificationUtil;

	private static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	public MiraklSellersExtractServiceImpl(final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient,
			final StrategyExecutor<MiraklShop, SellerModel> miraklShopSellerModelStrategyExecutor,
			final MailNotificationUtil sellerMailNotificationUtil) {
		this.miraklOperatorClient = miraklOperatorClient;
		this.miraklShopSellerModelStrategyExecutor = miraklShopSellerModelStrategyExecutor;
		this.sellerMailNotificationUtil = sellerMailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SellerModel> extractIndividuals(@Nullable final Date delta) {
		final MiraklShops shops = retrieveMiraklShopsByDate(delta);
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
		final MiraklShops shops = retrieveMiraklShopsByDate(delta);
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
				.map(miraklShopSellerModelStrategyExecutor::execute).collect(Collectors.toList());
	}

	@Override
	public List<SellerModel> extractSellers(@Nullable final Date delta) {
		final MiraklShops shops = retrieveMiraklShopsByDate(delta);
		//@formatter:off
		log.info(SHOPS_RETRIEVED_MESSAGE, Stream.ofNullable(shops.getShops())
				.flatMap(Collection::stream)
				.map(MiraklShop::getId)
				.collect(Collectors.joining(LoggingConstantsUtil.LIST_LOGGING_SEPARATOR)));

		return Stream.ofNullable(shops.getShops())
				.flatMap(Collection::stream)
				.map(miraklShopSellerModelStrategyExecutor::execute)
				.filter(SellerModel::hasAcceptedTermsAndConditions)
				.collect(Collectors.toList());

		//@formatter:on
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUserToken(final HyperwalletUser hyperwalletUser) {
		final MiraklUpdateShop miraklUpdateShop = new MiraklUpdateShop();
		miraklUpdateShop.setShopId(Long.valueOf(hyperwalletUser.getClientUserId()));
		final MiraklSimpleRequestAdditionalFieldValue userTokenCustomField = new MiraklSimpleRequestAdditionalFieldValue();
		userTokenCustomField.setCode(HYPERWALLET_USER_TOKEN);
		userTokenCustomField.setValue(hyperwalletUser.getToken());
		miraklUpdateShop.setAdditionalFieldValues(List.of(userTokenCustomField));
		final MiraklUpdateShopsRequest request = new MiraklUpdateShopsRequest(List.of(miraklUpdateShop));
		log.info("Updating token for shop [{}] to [{}]", hyperwalletUser.getClientUserId(), hyperwalletUser.getToken());
		try {
			miraklOperatorClient.updateShops(request);
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting information of shop [{}]", hyperwalletUser.getClientUserId());
			sellerMailNotificationUtil.sendPlainTextEmail(EMAIL_SUBJECT_MESSAGE,
					String.format(ERROR_MESSAGE_PREFIX + "Something went wrong getting information of shop [%s]%n%s",
							hyperwalletUser.getClientUserId(), MiraklLoggingErrorsUtil.stringify(ex)));
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
				.map(miraklShopSellerModelStrategyExecutor::execute)
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
				.map(miraklShopSellerModelStrategyExecutor::execute)
				.filter(SellerModel::hasAcceptedTermsAndConditions)
				.collect(Collectors.toList());
		//@formatter:on
	}

	private MiraklShops retrieveMiraklShopsByDate(@Nullable final Date delta) {
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		request.setUpdatedSince(delta);
		request.setPaginate(false);
		log.info("Retrieving shops since {}", delta);
		try {
			return miraklOperatorClient.getShops(request);
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
		try {
			return miraklOperatorClient.getShops(request);
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
