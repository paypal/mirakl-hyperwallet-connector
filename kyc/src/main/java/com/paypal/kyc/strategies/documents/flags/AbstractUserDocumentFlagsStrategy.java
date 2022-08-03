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

@Slf4j
public abstract class AbstractUserDocumentFlagsStrategy
		implements Strategy<KYCUserDocumentFlagsNotificationBodyModel, Void> {

	protected final MailNotificationUtil mailNotificationUtil;

	protected final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	protected static final String EMAIL_BODY_PREFIX = "There was an error, please check the logs for further information:\n";

	protected AbstractUserDocumentFlagsStrategy(final MailNotificationUtil mailNotificationUtil,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient) {
		this.mailNotificationUtil = mailNotificationUtil;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void execute(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return false;
	}

	protected void fillMiraklProofIdentityOrBusinessFlagStatus(final KYCUserDocumentFlagsNotificationBodyModel source) {
		final MiraklUpdateShop updateShop = new MiraklUpdateShop();

		final MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue additionalValue = new MiraklRequestAdditionalFieldValue.MiraklSimpleRequestAdditionalFieldValue(
				KYCConstants.HYPERWALLET_KYC_REQUIRED_PROOF_IDENTITY_BUSINESS_FIELD, Boolean.TRUE.toString());

		updateShop.setShopId(Long.valueOf(source.getClientUserId()));
		updateShop.setAdditionalFieldValues(List.of(additionalValue));

		try {
			log.info("Updating KYC proof of identity flag in Mirakl for shopId [{}]", source.getClientUserId());
			final MiraklUpdateShopsRequest miraklUpdateShopsRequest = new MiraklUpdateShopsRequest(List.of(updateShop));
			miraklMarketplacePlatformOperatorApiClient.updateShops(miraklUpdateShopsRequest);
			log.info("Proof of identity flag updated for shopId [{}]", source.getClientUserId());
		}
		catch (final MiraklException ex) {
			log.error(String.format("Something went wrong updating KYC information of shop [%s]. Details [%s]",
					source.getClientUserId(), ex.getMessage()), ex);
			mailNotificationUtil.sendPlainTextEmail("Issue detected updating KYC information in Mirakl",
					String.format(EMAIL_BODY_PREFIX + "Something went wrong updating KYC information of shop [%s]%n%s",
							source.getClientUserId(), MiraklLoggingErrorsUtil.stringify(ex)));
			// Rethrow exception to handle it in AbstractNotificationListener
			throw ex;
		}
	}

}
