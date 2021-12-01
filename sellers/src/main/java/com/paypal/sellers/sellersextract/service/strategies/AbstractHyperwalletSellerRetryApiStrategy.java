package com.paypal.sellers.sellersextract.service.strategies;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.entity.FailedSellersInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.AbstractHyperwalletRetryAPIStrategy;
import com.paypal.sellers.service.FailedEntityInformationService;
import com.paypal.sellers.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;

/**
 * Abstract Strategy class that covers API errors or possible retries when
 * creating/updating sellers into Hyperwallet
 */
@Slf4j
public abstract class AbstractHyperwalletSellerRetryApiStrategy
		extends AbstractHyperwalletRetryAPIStrategy<FailedSellersInformation>
		implements Strategy<SellerModel, HyperwalletUser> {

	protected final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter;

	protected final HyperwalletSDKService hyperwalletSDKService;

	protected final MailNotificationUtil mailNotificationUtil;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	protected AbstractHyperwalletSellerRetryApiStrategy(
			final FailedEntityInformationService<FailedSellersInformation> failedEntityInformationService,
			final Converter<SellerModel, HyperwalletUser> sellerModelHyperwalletUserConverter,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
		super(failedEntityInformationService);
		this.sellerModelHyperwalletUserConverter = sellerModelHyperwalletUserConverter;
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HyperwalletUser execute(final SellerModel seller) {
		boolean includedAsFailed = false;
		HyperwalletUser hyperwalletUser = null;
		final HyperwalletUser hwUserRequest = sellerModelHyperwalletUserConverter.convert(seller);
		log.debug("Hyperwallet API user request: [{}]", ToStringBuilder.reflectionToString(hwUserRequest));
		try {
			hyperwalletUser = createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(hwUserRequest);
			log.info("Seller created or updated for seller with clientId [{}]", seller.getClientUserId());
			log.debug("Hyperwallet created or updated seller response: [{}]",
					ToStringBuilder.reflectionToString(hwUserRequest));
		}
		catch (final HyperwalletException e) {
			if (e.getCause() instanceof IOException) {
				includedAsFailed = true;
			}
			mailNotificationUtil.sendPlainTextEmail("Issue detected when creating or updating seller in Hyperwallet",
					String.format(ERROR_MESSAGE_PREFIX + "Seller not created or updated with clientId [%s]%n%s",
							seller.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
			log.error("Seller not created or updated with clientId [{}]", seller.getClientUserId());
			log.error(HyperwalletLoggingErrorsUtil.stringify(e));
		}
		finally {
			callToIncludeIntoRetryProcess(seller, includedAsFailed);
		}

		return hyperwalletUser;
	}

	protected abstract HyperwalletUser createOrUpdateUserOnHyperWalletAndUpdateItsTokenOnMirakl(
			HyperwalletUser hyperwalletUser);

	protected void callToIncludeIntoRetryProcess(final SellerModel sellerModel, final boolean include) {
		executeRetryProcess(sellerModel.getClientUserId(), include);
	}

}
