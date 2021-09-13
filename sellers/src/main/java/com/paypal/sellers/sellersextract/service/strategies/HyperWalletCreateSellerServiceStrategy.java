package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
import com.paypal.sellers.service.FailedEntityInformationService;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Strategy class that manages sellers creation in hyperwallet and token update in Mirakl
 */
@Service
public class HyperWalletCreateSellerServiceStrategy extends AbstractHyperwalletSellerRetryApiStrategy {

	private final MiraklSellersExtractService miraklSellersExtractService;

	protected HyperWalletCreateSellerServiceStrategy(
			final FailedEntityInformationService<FailedSellersInformation> failedEntityInformationService,
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil,
			final MiraklSellersExtractService miraklSellersExtractService) {
		super(failedEntityInformationService, sellerModelHyperwalletUserConverter, hyperwalletSDKService,
				mailNotificationUtil);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * It creates the user on HyperWallet side, then it updates the user information on
	 * Mirakl accordingly with the response received on user creation by HyperWallet
	 * @param hyperwalletUser The User to be created
	 * @return The created HyperWallet user
	 */
	@Override
	protected HyperwalletUser createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(
			final HyperwalletUser hyperwalletUser) {
		final Hyperwallet hyperwallet = hyperwalletSDKService
				.getHyperwalletInstanceByProgramToken(hyperwalletUser.getProgramToken());
		final HyperwalletUser hwUser = hyperwallet.createUser(hyperwalletUser);
		miraklSellersExtractService.updateUserToken(hwUser);
		return hwUser;
	}

	/**
	 * Checks whether the strategy must be executed based on the {@code seller}
	 * @param seller the seller object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final SellerModel seller) {
		return Objects.isNull(seller.getToken());
	}

}
