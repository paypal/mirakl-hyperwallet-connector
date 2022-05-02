package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.HyperwalletSDKService;
import org.slf4j.Logger;

public abstract class AbstractHyperwalletSellerServiceStrategy implements Strategy<SellerModel, SellerModel> {

	protected final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter;

	protected final HyperwalletSDKService hyperwalletSDKService;

	protected final MailNotificationUtil mailNotificationUtil;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	protected AbstractHyperwalletSellerServiceStrategy(
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
		this.sellerModelHyperwalletUserConverter = sellerModelHyperwalletUserConverter;
		this.hyperwalletSDKService = hyperwalletSDKService;
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
