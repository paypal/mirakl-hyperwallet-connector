package com.paypal.sellers.sellerextractioncommons.services.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import org.slf4j.Logger;

public abstract class AbstractHyperwalletSellerServiceStrategy implements Strategy<SellerModel, SellerModel> {

	protected final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter;

	protected final UserHyperwalletSDKService userHyperwalletSDKService;

	protected final MailNotificationUtil mailNotificationUtil;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	protected AbstractHyperwalletSellerServiceStrategy(
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		this.sellerModelHyperwalletUserConverter = sellerModelHyperwalletUserConverter;
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SellerModel execute(final SellerModel seller) {
		final HyperwalletUser hwUserRequest = sellerModelHyperwalletUserConverter.convert(seller);
		final HyperwalletUser hyperwalletUser = pushToHyperwallet(hwUserRequest);

		return updateSellerToken(seller, hyperwalletUser);
	}

	private SellerModel updateSellerToken(final SellerModel seller, final HyperwalletUser hyperwalletUser) {
		//@formatter:off
		return seller.toBuilder()
				.token(hyperwalletUser.getToken())
				.build();
		//@formatter:on
	}

	protected abstract HyperwalletUser pushToHyperwallet(HyperwalletUser hyperwalletUser);

	protected void logErrors(final String message, final Exception e, final Logger log) {
		log.error(message, e);
	}

	protected void reportError(final String subject, final String message) {
		mailNotificationUtil.sendPlainTextEmail(subject, message);
	}

}
