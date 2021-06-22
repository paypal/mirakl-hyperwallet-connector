package com.paypal.kyc.strategies.documents.flags.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.kyc.model.KYCUserDocumentFlagsNotificationBodyModel;
import com.paypal.kyc.strategies.documents.flags.AbstractUserDocumentFlagsStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class KYCUserDocumentFlagIndividualStrategy extends AbstractUserDocumentFlagsStrategy {

	public KYCUserDocumentFlagIndividualStrategy(
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient,
			final MailNotificationUtil mailNotificationUtil) {
		super(mailNotificationUtil, miraklMarketplacePlatformOperatorApiClient);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Void> execute(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return superFillMiraklProofIdentityOrBusinessFlagStatus(source);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserDocumentFlagsNotificationBodyModel source) {
		return HyperwalletUser.ProfileType.INDIVIDUAL.equals(source.getProfileType())
				&& HyperwalletUser.VerificationStatus.REQUIRED.equals(source.getVerificationStatus());
	}

	protected Optional<Void> superFillMiraklProofIdentityOrBusinessFlagStatus(
			KYCUserDocumentFlagsNotificationBodyModel notification) {
		return fillMiraklProofIdentityOrBusinessFlagStatus(notification);
	}

}
