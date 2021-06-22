package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCRejectionReasonService;
import com.paypal.kyc.strategies.status.AbstractKYCUserStatusNotificationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;

@Service
@Slf4j
public class IndividualKYCUserStatusNotificationStrategy extends AbstractKYCUserStatusNotificationStrategy {

	private EnumMap<HyperwalletUser.VerificationStatus, MiraklShopKycStatus> statusMapping;

	public IndividualKYCUserStatusNotificationStrategy(
			final MiraklMarketplacePlatformOperatorApiClient miraklOperatorClient,
			final MailNotificationUtil mailNotificationUtil,
			final KYCRejectionReasonService kycRejectionReasonService) {
		super(miraklOperatorClient, mailNotificationUtil, kycRejectionReasonService);
		initializeMap();
	}

	@Override
	protected MiraklShopKycStatus expectedKycMiraklStatus(
			final KYCUserStatusNotificationBodyModel incomingNotification) {
		return statusMapping.get(incomingNotification.getVerificationStatus());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserStatusNotificationBodyModel source) {
		return (HyperwalletUser.ProfileType.INDIVIDUAL.equals(source.getProfileType()));
	}

	private void initializeMap() {
		statusMapping = new EnumMap<>(HyperwalletUser.VerificationStatus.class);
		statusMapping.put(HyperwalletUser.VerificationStatus.UNDER_REVIEW, MiraklShopKycStatus.PENDING_APPROVAL);
		statusMapping.put(HyperwalletUser.VerificationStatus.VERIFIED, MiraklShopKycStatus.APPROVED);
		statusMapping.put(HyperwalletUser.VerificationStatus.REQUIRED, MiraklShopKycStatus.REFUSED);
		statusMapping.put(HyperwalletUser.VerificationStatus.NOT_REQUIRED, MiraklShopKycStatus.APPROVED);
	}

}
