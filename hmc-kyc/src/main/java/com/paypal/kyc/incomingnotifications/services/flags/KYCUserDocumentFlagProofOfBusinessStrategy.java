package com.paypal.kyc.incomingnotifications.services.flags;

import com.hyperwallet.clientsdk.model.HyperwalletUser;

import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.incomingnotifications.model.KYCUserDocumentFlagsNotificationBodyModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KYCUserDocumentFlagProofOfBusinessStrategy extends AbstractUserDocumentFlagsStrategy {

	public KYCUserDocumentFlagProofOfBusinessStrategy(final MiraklClient miraklMarketplacePlatformOperatorApiClient,
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
			final KYCUserDocumentFlagsNotificationBodyModel notification) {
		super.fillMiraklProofIdentityOrBusinessFlagStatus(notification);
	}

}
