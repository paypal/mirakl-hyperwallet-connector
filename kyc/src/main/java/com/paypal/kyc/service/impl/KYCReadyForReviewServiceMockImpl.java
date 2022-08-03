package com.paypal.kyc.service.impl;

import com.google.gson.Gson;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.exceptions.HMCHyperwalletAPIException;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link KYCReadyForReviewServiceImpl} for mocking purposes
 */
@Profile({ "qa" })
@Slf4j
@Service("KYCReadyForReviewService")
public class KYCReadyForReviewServiceMockImpl extends KYCReadyForReviewServiceImpl {

	private static final String HYPERWALLET_NOTIFY_USER = "/hyperwallet/v4/{userToken}";

	private final String mockServerUrl;

	private final RestTemplate restTemplate;

	public KYCReadyForReviewServiceMockImpl(@Value("${mockserver.url}") final String mockServerUrl,
			final RestTemplate restTemplate, final HyperwalletSDKService hyperwalletSDKService,
			final MailNotificationUtil kycMailNotificationUtil) {
		super(hyperwalletSDKService, kycMailNotificationUtil);
		this.restTemplate = restTemplate;
		this.mockServerUrl = mockServerUrl;
	}

	@Override
	public void notifyReadyForReview(KYCDocumentInfoModel kycDocumentInfoModel) {
		final String token = kycDocumentInfoModel.getUserToken();
		final HyperwalletUser user = new HyperwalletUser();
		user.setToken(token);
		user.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);

		final String postURL = HYPERWALLET_NOTIFY_USER.replace("{userToken}", token);
		try {
			final Gson gsonConverter = new Gson();
			restTemplate.put(getMockServerUrl() + postURL, gsonConverter.toJson(user), Object.class);
			log.info("Pushed successfully to mockserver business stakeholder notification update shopId [{}]",
					kycDocumentInfoModel.getClientUserId());
		}
		catch (HyperwalletException e) {
			final String clientUserId = kycDocumentInfoModel.getClientUserId();

			log.error(String.format("Error notifying to Hyperwallet that all documents were sent.%n%s",
					HyperwalletLoggingErrorsUtil.stringify(e)), e);
			getKycMailNotificationUtil().sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
					"There was an error notifying Hyperwallet all documents were sent for shop Id [%s], so Hyperwallet will not be notified about this new situation%n%s",
					clientUserId, HyperwalletLoggingErrorsUtil.stringify(e)));

			throw new HMCHyperwalletAPIException(e);
		}
	}

	protected String getMockServerUrl() {
		return mockServerUrl;
	}

}
