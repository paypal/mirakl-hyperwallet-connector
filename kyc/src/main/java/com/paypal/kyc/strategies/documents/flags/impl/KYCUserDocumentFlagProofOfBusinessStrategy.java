package com.paypal.kyc.strategies.documents.flags.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.strategies.documents.flags.AbstractUserDocumentFlagsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KYCUserDocumentFlagProofOfBusinessStrategy extends AbstractUserDocumentFlagsStrategy {

	public KYCUserDocumentFlagProofOfBusinessStrategy(
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient,
			final MailNotificationUtil mailNotificationUtil) {
		super(mailNotificationUtil, miraklMarketplacePlatformOperatorApiClient);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void execute(final KYCUserDocumentFlagsNotificationBodyModel source) {
		superFillMiraklProofIdentityOrBusinessFlagStatus(source);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return HyperwalletUser.ProfileType.BUSINESS.equals(source.getProfileType())
				&& HyperwalletUser.VerificationStatus.REQUIRED.equals(source.getVerificationStatus());
	}

	protected void superFillMiraklProofIdentityOrBusinessFlagStatus(
			KYCUserDocumentFlagsNotificationBodyModel notification) {
		super.fillMiraklProofIdentityOrBusinessFlagStatus(notification);
	}

}
