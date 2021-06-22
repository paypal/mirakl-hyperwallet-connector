package com.paypal.sellers.bankaccountextract.service.strategies;

import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletBankAccount;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.strategy.StrategyFactory;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.sellersextract.model.SellerModel;
import com.paypal.sellers.service.AbstractHyperwalletRetryAPIStrategy;
import com.paypal.sellers.service.FailedEntityInformationService;
import com.paypal.sellers.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractHyperwalletBankAccountRetryApiStrategy
		extends AbstractHyperwalletRetryAPIStrategy<FailedBankAccountInformation>
		implements Strategy<SellerModel, Optional<HyperwalletBankAccount>> {

	protected final StrategyFactory<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyFactory;

	protected final HyperwalletSDKService hyperwalletSDKService;

	protected final MailNotificationUtil mailNotificationUtil;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further "
			+ "information:\n";

	protected AbstractHyperwalletBankAccountRetryApiStrategy(
			final FailedEntityInformationService<FailedBankAccountInformation> failedEntityInformationService,
			final StrategyFactory<SellerModel, HyperwalletBankAccount> sellerModelToHyperwalletBankAccountStrategyFactory,
			final HyperwalletSDKService hyperwalletSDKService, final MailNotificationUtil mailNotificationUtil) {
		super(failedEntityInformationService);
		this.sellerModelToHyperwalletBankAccountStrategyFactory = sellerModelToHyperwalletBankAccountStrategyFactory;
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.mailNotificationUtil = mailNotificationUtil;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<HyperwalletBankAccount> execute(final SellerModel seller) {
		boolean includedAsFailed = false;
		HyperwalletBankAccount hwCreatedBankAccount = null;
		final HyperwalletBankAccount hwBankAccountRequest = sellerModelToHyperwalletBankAccountStrategyFactory
				.execute(seller);
		if (Objects.nonNull(hwBankAccountRequest)) {
			log.debug("Hyperwallet API bank account request: [{}]",
					ToStringBuilder.reflectionToString(hwBankAccountRequest));
			try {
				hwCreatedBankAccount = callMiraklAPI(seller.getHyperwalletProgram(), hwBankAccountRequest);
				log.info("Bank account created or updated for seller with clientId [{}]", seller.getClientUserId());
				log.debug("Hyperwallet create or updated bank account response: [{}]",
						ToStringBuilder.reflectionToString(hwBankAccountRequest));
			}
			catch (final HyperwalletException e) {
				if (e.getCause() instanceof IOException) {
					includedAsFailed = true;
				}
				mailNotificationUtil
						.sendPlainTextEmail("Issue detected when creating or updating bank account in Hyperwallet",
								String.format(ERROR_MESSAGE_PREFIX
										+ "Bank account not created or updated for seller with clientId [%s]%n%s",
										seller.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
				log.error("Bank account not created or updated for seller with clientId [{}]",
						seller.getClientUserId());
				log.error(HyperwalletLoggingErrorsUtil.stringify(e));
			}
			finally {
				callToIncludeIntoRetryProcess(seller, includedAsFailed);
			}
		}
		return Optional.ofNullable(hwCreatedBankAccount);
	}

	protected abstract HyperwalletBankAccount callMiraklAPI(final String hyperwalletProgram,
			HyperwalletBankAccount hyperwalletBankAccount);

	protected void callToIncludeIntoRetryProcess(final SellerModel sellerModel, final Boolean include) {
		executeRetryProcess(sellerModel.getClientUserId(), include);
	}

}
