package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.hyperwallet.api.HyperwalletSDKUserService;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.KYCReadyForReviewService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link KYCReadyForReviewService}
 */
@Profile({ "!qa" })
@Slf4j
@Getter
@Service
public class KYCReadyForReviewServiceImpl implements KYCReadyForReviewService {

	private final HyperwalletSDKUserService hyperwalletSDKUserService;

	private final MailNotificationUtil kycMailNotificationUtil;

	public KYCReadyForReviewServiceImpl(final HyperwalletSDKUserService hyperwalletSDKUserService,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.hyperwalletSDKUserService = hyperwalletSDKUserService;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	@Override
	public void notifyReadyForReview(final KYCDocumentInfoModel kycDocumentInfoModel) {
		final String token = kycDocumentInfoModel.getUserToken();
		final HyperwalletUser user = new HyperwalletUser();
		user.setToken(token);
		user.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);

		try {
			final String hyperwalletProgram = kycDocumentInfoModel.getHyperwalletProgram();
			if (StringUtils.isNotEmpty(hyperwalletProgram)) {
				final Hyperwallet hyperwallet = hyperwalletSDKUserService
						.getHyperwalletInstanceByHyperwalletProgram(hyperwalletProgram);
				final HyperwalletUser hyperwalletUser = hyperwallet.updateUser(user);
				log.info("Seller with id [{}] has been set as Ready for review", hyperwalletUser.getClientUserId());
			}
			else {
				log.error("Seller with shop Id [{}] has no Hyperwallet Program",
						kycDocumentInfoModel.getClientUserId());
			}

		}
		catch (final HyperwalletException e) {
			reportHyperwalletAPIError(kycDocumentInfoModel, e);

			throw new HMCHyperwalletAPIException(e);
		}
	}

	private void reportHyperwalletAPIError(final KYCDocumentInfoModel kycDocumentInfoModel,
			final HyperwalletException e) {
		log.error(String.format("Error notifying to Hyperwallet that all documents were sent.%n%s",
				HyperwalletLoggingErrorsUtil.stringify(e)), e);

		kycMailNotificationUtil.sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
				"There was an error notifying Hyperwallet all documents were sent for shop Id [%s], so Hyperwallet will not be notified about this new situation%n%s",
				kycDocumentInfoModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
	}

}
