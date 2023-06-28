package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import com.paypal.sellers.sellerextractioncommons.services.MiraklSellersExtractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Strategy class that manages sellers creation in hyperwallet and token update in Mirakl
 */
@Slf4j
@Service
public class HyperWalletCreateSellerServiceStrategy extends AbstractHyperwalletSellerServiceStrategy {

	private final MiraklSellersExtractService miraklSellersExtractService;

	protected HyperWalletCreateSellerServiceStrategy(
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final UserHyperwalletSDKService userHyperwalletSDKService, final MailNotificationUtil mailNotificationUtil,
			final MiraklSellersExtractService miraklSellersExtractService) {
		super(sellerModelHyperwalletUserConverter, userHyperwalletSDKService, mailNotificationUtil);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * It creates the user on HyperWallet side and updates the token in Mirakl
	 * @param hyperwalletUser The User to be created
	 * @return The created HyperWallet user
	 */
	@Override
	protected HyperwalletUser pushToHyperwallet(final HyperwalletUser hyperwalletUser) {
		final Hyperwallet hyperwallet = userHyperwalletSDKService
				.getHyperwalletInstanceByProgramToken(hyperwalletUser.getProgramToken());
		try {
			final HyperwalletUser hwUser = hyperwallet.createUser(hyperwalletUser);

			log.info("Seller created for seller with clientUserId [{}]", hyperwalletUser.getClientUserId());

			miraklSellersExtractService.updateUserToken(hwUser);

			return hwUser;
		}
		catch (final HyperwalletException e) {
			logErrors("Error creating seller in hyperwallet with clientUserId [%s].%n%s"
					.formatted(hyperwalletUser.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e, log);
			reportError("Issue detected when creating seller in Hyperwallet",
					(ERROR_MESSAGE_PREFIX + "Seller not created with clientId [%s]%n%s")
							.formatted(hyperwalletUser.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));

			throw new HMCHyperwalletAPIException(e);
		}
		catch (final MiraklException e) {
			logErrors("Error updating token in mirakl with clientUserId [%s]: [{}]"
					.formatted(hyperwalletUser.getClientUserId()), e, log);
			reportError("Issue detected when updating seller in Mirakl",
					(ERROR_MESSAGE_PREFIX + "Seller token not updated with clientId [%s]%n%s")
							.formatted(hyperwalletUser.getClientUserId(), MiraklLoggingErrorsUtil.stringify(e)));
			throw new HMCMiraklAPIException(e);
		}
	}

	/**
	 * Checks whether the strategy must be executed based on the not existence of the
	 * {@code seller}
	 * @param seller the seller object
	 * @return returns whether the strategy is applicable or not
	 */
	@Override
	public boolean isApplicable(final SellerModel seller) {
		return Objects.isNull(seller.getToken());
	}

}
