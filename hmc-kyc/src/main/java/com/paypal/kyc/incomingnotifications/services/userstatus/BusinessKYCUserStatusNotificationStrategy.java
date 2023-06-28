package com.paypal.kyc.incomingnotifications.services.userstatus;

import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.mirakl.client.mmp.domain.shop.MiraklShopKycStatus;

import com.paypal.infrastructure.support.converter.Converter;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.mirakl.client.MiraklClient;
import com.paypal.kyc.incomingnotifications.model.KYCDocumentNotificationModel;
import com.paypal.kyc.incomingnotifications.model.KYCUserStatusNotificationBodyModel;
import com.paypal.kyc.incomingnotifications.services.KYCRejectionReasonService;
import com.paypal.kyc.sellersdocumentextraction.services.MiraklSellerDocumentsExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BusinessKYCUserStatusNotificationStrategy extends AbstractKYCUserStatusNotificationStrategy {

	private static final String REQUIRED = "REQUIRED";

	private static final String UNDER_REVIEW = "UNDER_REVIEW";

	public BusinessKYCUserStatusNotificationStrategy(final MiraklClient miraklOperatorClient,
			final MailNotificationUtil mailNotificationUtil, final KYCRejectionReasonService kycRejectionReasonService,
			final MiraklSellerDocumentsExtractService miraklSellerDocumentsExtractService,
			final Converter<KYCDocumentNotificationModel, List<String>> kycDocumentNotificationModelListConverter) {
		super(miraklOperatorClient, mailNotificationUtil, kycRejectionReasonService,
				miraklSellerDocumentsExtractService, kycDocumentNotificationModelListConverter);
	}

	@Override
	protected MiraklShopKycStatus expectedKycMiraklStatus(
			final KYCUserStatusNotificationBodyModel incomingNotification) {
		final String verificationStatus = Optional.ofNullable(incomingNotification.getVerificationStatus())
				.map(Enum::name).orElse(null);
		final String businessStakeHolderStatus = Optional
				.ofNullable(incomingNotification.getBusinessStakeholderVerificationStatus()).map(Enum::name)
				.orElse(null);
		final String letterStatus = Optional.ofNullable(incomingNotification.getLetterOfAuthorizationStatus())
				.map(Enum::name).orElse(null);

		final Triple<String, String, String> statuses = Triple.of(verificationStatus, businessStakeHolderStatus,
				letterStatus);
		if (REQUIRED.equals(statuses.getLeft()) || REQUIRED.equals(statuses.getMiddle())
				|| REQUIRED.equals(statuses.getRight())) {
			return MiraklShopKycStatus.REFUSED;
		}
		if (UNDER_REVIEW.equals(statuses.getLeft()) || UNDER_REVIEW.equals(statuses.getMiddle())
				|| UNDER_REVIEW.equals(statuses.getRight())) {
			return MiraklShopKycStatus.PENDING_APPROVAL;
		}
		return MiraklShopKycStatus.APPROVED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isApplicable(final KYCUserStatusNotificationBodyModel source) {
		return (HyperwalletUser.ProfileType.BUSINESS.equals(source.getProfileType()));
	}

}
