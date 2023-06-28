package com.paypal.sellers.bankaccountextraction.services.strategies;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.mirakl.client.core.exception.MiraklApiException;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.exceptions.HMCMiraklAPIException;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.strategy.Strategy;
import com.paypal.infrastructure.support.strategy.StrategyExecutor;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.infrastructure.support.logging.MiraklLoggingErrorsUtil;
import com.paypal.sellers.sellerextractioncommons.model.SellerModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractHyperwalletBankAccountStrategy
		implements Strategy<SellerModel, Optional<HyperwalletBankAccount>> {

	protected final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor;

	protected final UserHyperwalletSDKService userHyperwalletSDKService;

	protected final MailNotificationUtil mailNotificationUtil;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	protected AbstractHyperwalletBankAccountStrategy(
			final StrategyExecutor<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyExecutor,
			final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil mailNotificationUtil) {
		this.sellerModelToHyperwalletBankAccountStrategyExecutor = sellerModelToHyperwalletBankAccountStrategyExecutor;
		this.userHyperwalletSDKService = userHyperwalletSDKService;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<HyperwalletBankAccount> execute(final SellerModel seller) {
		HyperwalletBankAccount hwCreatedBankAccount = null;
		final HyperwalletBankAccount hwBankAccountRequest = sellerModelToHyperwalletBankAccountStrategyExecutor
				.execute(seller);
		if (Objects.nonNull(hwBankAccountRequest)) {
			try {
				hwCreatedBankAccount = callHyperwalletAPI(seller.getHyperwalletProgram(), hwBankAccountRequest);
				log.info("Bank account created or updated for seller with clientId [{}]", seller.getClientUserId());
			}
			catch (final HyperwalletException e) {

				mailNotificationUtil
						.sendPlainTextEmail("Issue detected when creating or updating bank account in Hyperwallet",
								String.format(ERROR_MESSAGE_PREFIX
										+ "Bank account not created or updated for seller with clientId [%s]%n%s",
										seller.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
				log.error(String.format("Bank account not created or updated for seller with clientId [%s].%n%s",
						seller.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)), e);

				throw new HMCHyperwalletAPIException(e);

			}

			catch (final MiraklApiException e) {

				log.error("Error while updating Mirakl bank account by clientUserId [{}]", seller.getClientUserId(), e);

				log.error(MiraklLoggingErrorsUtil.stringify(e));

				throw new HMCMiraklAPIException(e);
			}
		}
		return Optional.ofNullable(hwCreatedBankAccount);
	}

	protected abstract HyperwalletBankAccount callHyperwalletAPI(final String hyperwalletProgram,
			HyperwalletBankAccount hyperwalletBankAccount);

}
