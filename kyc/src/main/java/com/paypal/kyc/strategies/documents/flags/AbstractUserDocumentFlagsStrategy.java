package com.paypal.kyc.strategies.documents.flags;

import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.domain.shop.update.MiraklUpdateShop;
import com.mirakl.client.mmp.operator.request.shop.MiraklUpdateShopsRequest;
import com.mirakl.client.mmp.request.additionalfield.MiraklRequestAdditionalFieldValue;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.strategy.Strategy;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.kyc.model.KYCConstants;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractUserDocumentFlagsStrategy
		implements Strategy<KYCUserDocumentFlagsNotificationBodyModel, Optional<Void>> {

	protected final MailNotificationUtil mailNotificationUtil;

	protected final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	protected static final String ERROR_MESSAGE_PREFIX = "There was an error, please check the logs for further information:\n";

	protected AbstractUserDocumentFlagsStrategy(final MailNotificationUtil mailNotificationUtil,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient) {
		this.mailNotificationUtil = mailNotificationUtil;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Void> execute(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return false;
	}

	protected Optional<Void> fillMiraklProofIdentityOrBusinessFlagStatus(
			final KYCUserDocumentFlagsNotificationBodyModel source) {
		final MiraklUpdateShop updateShop = new MiraklUpdateShop();

		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue additionalValue = new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
				KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD, Boolean.TRUE.toString());

		updateShop.setShopId(Long.valueOf(source.getClientUserId()));
		updateShop.setAdditionalFieldValues(List.of(additionalValue));

		try {
			log.debug("Updating KYC proof of identity flag in Mirakl for shopId [{}]", source.getClientUserId());
			final MiraklUpdateShopsRequest miraklUpdateShopsRequest = new MiraklUpdateShopsRequest(List.of(updateShop));
			miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
			log.info("Proof of identity flag updated for shopId [{}]", source.getClientUserId());
		}
		catch (final MiraklException ex) {
			log.error("Something went wrong updating KYC information of shop [{}]. Details [{}]",
					source.getClientUserId(), ex.getMessage());
			mailNotificationUtil.sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
					String.format(
							ERROR_MESSAGE_PREFIX + "Something went wrong updating KYC information of shop [%s]%n%s",
							source.getClientUserId(), MiraklLoggingErrorsUtil.stringify(ex)));
		}
		return Optional.empty();
	}

}
