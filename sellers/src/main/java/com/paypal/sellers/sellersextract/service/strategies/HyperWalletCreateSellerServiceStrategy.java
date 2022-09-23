package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.core.exception.MiraklException;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.sellersextract.service.MiraklSellersExtractService;
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
			final HyperwalletSDKUserService hyperwalletSDKUserService, final MailNotificationUtil mailNotificationUtil,
			final MiraklSellersExtractService miraklSellersExtractService) {
		super(sellerModelHyperwalletUserConverter, hyperwalletSDKUserService, mailNotificationUtil);
		this.miraklSellersExtractService = miraklSellersExtractService;
	}

	/**
	 * It creates the user on HyperWallet side and updates the token in Mirakl
	 * @param hyperwalletUser The User to be created
	 * @return The created HyperWallet user
	 */
	@Override
	protected HyperwalletUser pushToHyperwallet(final HyperwalletUser hyperwalletUser) {
		final Hyperwallet hyperwallet = hyperwalletSDKUserService
				.getHyperwalletInstanceByProgramToken(hyperwalletUser.getProgramToken());
		try {
			final HyperwalletUser hwUser = hyperwallet.createUser(hyperwalletUser);

			log.info("Seller created for seller with clientUserId [{}]", hyperwalletUser.getClientUserId());

			miraklSellersExtractService.updateUserToken(hwUser);

			return hwUser;
		}
		catch (final HyperwalletException e) {
			logErrors(String.format("Error creating seller in hyperwallet with clientUserId [%s].%n%s",
					hyperwalletUser.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e, log);
			reportError("Issue detected when creating seller in Hyperwallet",
					String.format(ERROR_MESSAGE_PREFIX + "Seller not created with clientId [%s]%n%s",
							hyperwalletUser.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));

			throw new HMCHyperwalletAPIException(e);
		}
		catch (final MiraklException e) {
			logErrors(String.format("Error updating token in mirakl with clientUserId [%s]: [{}]",
					hyperwalletUser.getClientUserId()), e, log);
			reportError("Issue detected when updating seller in Mirakl",
					String.format(ERROR_MESSAGE_PREFIX + "Seller token not updated with clientId [%s]%n%s",
							hyperwalletUser.getClientUserId(), MiraklLoggingErrorsUtil.stringify(e)));
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
