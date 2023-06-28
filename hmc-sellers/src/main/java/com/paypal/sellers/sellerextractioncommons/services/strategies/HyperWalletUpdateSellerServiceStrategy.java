package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Strategy class that manages sellers update in hyperwallet
 */
@Slf4j
@Service
public class HyperWalletUpdateSellerServiceStrategy extends AbstractHyperwalletSellerServiceStrategy {

	protected HyperWalletUpdateSellerServiceStrategy(
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		super(sellerModelHyperwalletUserConverter, userHyperwalletSDKService, mailNotificationUtil);
	}

	@Override
	protected HyperwalletUser pushToHyperwallet(final HyperwalletUser hyperwalletUser) {

		try {
			final Hyperwallet hyperwallet = userHyperwalletSDKService
					.getHyperwalletInstanceByProgramToken(hyperwalletUser.getProgramToken());
			final HyperwalletUser updatedUser = hyperwallet.updateUser(hyperwalletUser);

			log.info("Seller updated for seller with clientUserId [{}]", hyperwalletUser.getClientUserId());

			return updatedUser;
		}
		catch (final HyperwalletException e) {
			logErrors("Error updating seller in hyperwallet with clientUserId [%s].%n%s"
					.formatted(hyperwalletUser.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e, log);

			reportError("Issue detected when updating seller in Hyperwallet",
					(ERROR_MESSAGE_PREFIX + "Seller not updated with clientId [%s]%n%s")
							.formatted(hyperwalletUser.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));

			throw new HMCHyperwalletAPIException(e);
		}
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
