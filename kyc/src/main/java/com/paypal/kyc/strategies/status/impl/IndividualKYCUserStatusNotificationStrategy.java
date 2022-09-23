package com.paypal.kyc.strategies.status.impl;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.sdk.mirakl.MiraklMarketplacePlatformOperatorApiWrapper;
import com.paypal.kyc.model.KYCDocumentNotificationModel;
import com.paypal.kyc.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.service.KYCRejectionReasonService;
import com.paypal.kyc.service.documents.files.mirakl.MiraklSellerDocumentsExtractService;
import com.paypal.kyc.strategies.status.AbstractKYCUserStatusNotificationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;

@Service
@Slf4j
public class IndividualKYCUserStatusNotificationStrategy extends AbstractKYCUserStatusNotificationStrategy {

	private EnumMap<HyperwalletUser.VerificationStatus, MiraklShopKycStatus> statusMapping;

	public IndividualKYCUserStatusNotificationStrategy(
			final MiraklMarketplacePlatformOperatorApiWrapper miraklOperatorClient,
			final MailNotificationUtil mailNotificationUtil, final KYCRejectionReasonService kycRejectionReasonService,
			final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverter) {
		super(miraklOperatorClient, mailNotificationUtil, kycRejectionReasonService,
				miraklSellerDocumentsExtractService, kycDocumentNotificationModelListConverter);
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
