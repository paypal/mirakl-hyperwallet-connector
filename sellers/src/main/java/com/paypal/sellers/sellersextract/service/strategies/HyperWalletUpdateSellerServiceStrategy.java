package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.FailedEntityInformationService;
import com.paypal.sellers.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Strategy class that manages sellers update in hyperwallet
 */
@Slf4j
@Service
public class HyperWalletUpdateSellerServiceStrategy extends AbstractHyperwalletSellerRetryApiStrategy {

	protected HyperWalletUpdateSellerServiceStrategy(
			final FailedEntityInformationService<FailedSellersInformation> failedEntityInformationService,
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
		super(failedEntityInformationService, sellerModelHyperwalletUserConverter, hyperwalletSDKService,
				mailNotificationUtil);
	}

	@Override
	protected HyperwalletUser createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(
			final HyperwalletUser hyperwalletUser) {
		final Hyperwallet hyperwallet = hyperwalletSDKService
				.getHyperwalletInstanceByProgramToken(hyperwalletUser.getProgramToken());
		final HyperwalletUser updatedUser = hyperwallet.updateUser(hyperwalletUser);
		log.info("Seller updated for seller with clientUserId [{}]", hyperwalletUser.getClientUserId());
		return updatedUser;
	}

	/**
	 * Checks whether the strategy must be executed based on the {@code seller}
	 * @param seller the seller object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final SellerModel seller) {
		return Objects.nonNull(seller.getToken());
	}

}
