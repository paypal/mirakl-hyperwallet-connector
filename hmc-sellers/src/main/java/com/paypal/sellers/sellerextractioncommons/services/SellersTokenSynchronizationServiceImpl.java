package com.paypal.sellers.sellerextractioncommons.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletList;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletUsersListPaginationOptions;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.services.TokenSynchronizationService;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class that implements the {@link TokenSynchronizationService} interface for the
 * synchronization of tokens for sellers
 */
@Slf4j
@Service("sellersTokenSynchronizationService")
public class SellersTokenSynchronizationServiceImpl implements TokenSynchronizationService<SellerModel> {

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	private final MiraklSellersExtractService miraklSellersExtractService;

	public SellersTokenSynchronizationServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final MiraklSellersExtractService miraklSellersExtractService) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * Ensures the seller's token between Hyperwallet and Mirakl is synchronized
	 * @param sellerModel that contains the seller item to be synchronized
	 * @return the seller with the seller's token synchronized
	 */
	@Override
	public SellerModel synchronizeToken(final SellerModel sellerModel) {
		if (StringUtils.isNotBlank(sellerModel.getToken())) {
			log.debug("Hyperwallet token already exists for client user id [{}], synchronization not needed",
					sellerModel.getClientUserId());

			return sellerModel;
		}

		final Optional<HyperwalletUser> hyperwalletUser = getHwUser(sellerModel);

		if (hyperwalletUser.isPresent()) {
			updateTokenInMirakl(hyperwalletUser.get());

			return updateSellerWithHyperwalletToken(sellerModel, hyperwalletUser.get());
		}
		else {
			return sellerModel;
		}
	}

	private void updateTokenInMirakl(final HyperwalletUser hyperwalletUser) {
		try {
			miraklSellersExtractService.updateUserToken(hyperwalletUser);
		}
		catch (final MiraklException e) {
			log.error("Error while updating Mirakl user by clientUserId [%s]"
					.formatted(hyperwalletUser.getClientUserId()), e);
			throw new HMCMiraklAPIException(e);
		}
	}

	private Optional<HyperwalletUser> getHwUser(final SellerModel sellerModel) {
		final HyperwalletList<HyperwalletUser> hyperwalletUserHyperwalletList = getHwUserByClientUserId(sellerModel);
		if (CollectionUtils.isEmpty(hyperwalletUserHyperwalletList.getData())) {
			log.debug("Hyperwallet user with client user id [{}] not found", sellerModel.getClientUserId());
			return Optional.empty();
		}
		else {
			log.debug("Hyperwallet user with client user id [{}] found", sellerModel.getClientUserId());
			return Optional.of(hyperwalletUserHyperwalletList.getData().get(0));
		}
	}

	private HyperwalletList<HyperwalletUser> getHwUserByClientUserId(final SellerModel sellerModel) {
		final Hyperwallet hyperwalletSDK = userHyperwalletSDKService
				.getHyperwalletInstanceByProgramToken(sellerModel.getProgramToken());
		final HyperwalletUsersListPaginationOptions paginationOptions = new HyperwalletUsersListPaginationOptions();
		paginationOptions.setClientUserId(sellerModel.getClientUserId());
		try {
			return hyperwalletSDK.listUsers(paginationOptions);
		}
		catch (final HyperwalletException e) {
			log.error(String.format("Error while getting Hyperwallet user by clientUserId [%s].%n%s",
					sellerModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e);

			throw new HMCHyperwalletAPIException(e);
		}
	}

	private SellerModel updateSellerWithHyperwalletToken(final SellerModel sellerModel,
			final HyperwalletUser hyperwalletUser) {
		return sellerModel.toBuilder().token(hyperwalletUser.getToken()).build();
	}

}
