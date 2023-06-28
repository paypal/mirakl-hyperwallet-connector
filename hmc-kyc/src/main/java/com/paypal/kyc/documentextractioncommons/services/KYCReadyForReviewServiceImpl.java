package com.paypal.kyc.documentextractioncommons.services;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.hyperwallet.services.UserHyperwalletSDKService;
import com.paypal.infrastructure.mail.services.MailNotificationUtil;
import com.paypal.infrastructure.support.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.support.logging.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.documentextractioncommons.model.KYCDocumentInfoModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link KYCReadyForReviewService}
 */
@Slf4j
@Getter
@Service
public class KYCReadyForReviewServiceImpl implements KYCReadyForReviewService {

	private final UserHyperwalletSDKService userHyperwalletSDKService;

	private final MailNotificationUtil kycMailNotificationUtil;

	public KYCReadyForReviewServiceImpl(final UserHyperwalletSDKService userHyperwalletSDKService,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.userHyperwalletSDKService = userHyperwalletSDKService;
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
				final Hyperwallet hyperwallet = userHyperwalletSDKService
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
		log.error("Error notifying to Hyperwallet that all documents were sent.%n%s"
				.formatted(HyperwalletLoggingErrorsUtil.stringify(e)), e);

		kycMailNotificationUtil.sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
				"There was an error notifying Hyperwallet all documents were sent for shop Id [%s], so Hyperwallet will not be notified about this new situation%n%s",
				kycDocumentInfoModel.getClientUserId(), HyperwalletLoggingErrorsUtil.stringify(e)));
	}

}
