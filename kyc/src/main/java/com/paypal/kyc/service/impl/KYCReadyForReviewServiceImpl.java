package com.paypal.kyc.service.impl;

import com.hyperwallet.clientsdk.Hyperwallet;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.service.KYCReadyForReviewService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link KYCReadyForReviewService}
 */
@Profile({ "!qa" })
@Slf4j
@Getter
@Service
public class KYCReadyForReviewServiceImpl implements KYCReadyForReviewService {

	private final HyperwalletSDKService hyperwalletSDKService;

	private final MailNotificationUtil kycMailNotificationUtil;

	public KYCReadyForReviewServiceImpl(final HyperwalletSDKService hyperwalletSDKService,
			final MailNotificationUtil kycMailNotificationUtil) {
		this.hyperwalletSDKService = hyperwalletSDKService;
		this.kycMailNotificationUtil = kycMailNotificationUtil;
	}

	@Override
	public void notifyReadyForReview(final List<KYCDocumentInfoModel> documentsTriedToBeSent) {
		//@formatter:off
		final Map<String, List<KYCDocumentInfoModel>> bstkGroupedByClientId = documentsTriedToBeSent.stream()
				.collect(Collectors.groupingBy(KYCDocumentInfoModel::getUserToken));

		final Map<String, List<KYCDocumentInfoModel>> userWithBstkToBeNotified = bstkGroupedByClientId.entrySet().stream()
				.filter(entry -> entry.getValue().stream()
						.allMatch(KYCDocumentInfoModel::isSentToHyperwallet))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		userWithBstkToBeNotified.entrySet()
				.forEach(this::notifyBstkReadyForReview);
		//@formatter:on
	}

	protected void notifyBstkReadyForReview(final Map.Entry<String, List<KYCDocumentInfoModel>> entry) {
		final String token = entry.getKey();
		final HyperwalletUser user = new HyperwalletUser();
		user.setToken(token);
		user.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);

		try {
			final Optional<String> hyperwalletProgramOptional = getHyperwalletProgram(entry.getValue());
			if (hyperwalletProgramOptional.isPresent()) {
				final String hyperwalletProgram = hyperwalletProgramOptional.get();
				final Hyperwallet hyperwallet = hyperwalletSDKService.getHyperwalletInstance(hyperwalletProgram);
				final HyperwalletUser hyperwalletUser = hyperwallet.updateUser(user);
				log.info("Seller with id [{}] has been set as Ready for review", hyperwalletUser.getClientUserId());
			}
			else {
				log.error("Seller with shop Id [{}] has no Hyperwallet Program", getClientId(entry.getValue()));
			}

		}
		catch (final HyperwalletException e) {
			//@formatter:off
			final String clientUserId = CollectionUtils.emptyIfNull(entry.getValue())
					.stream()
					.map(KYCDocumentInfoModel::getClientUserId)
					.findAny()
					.orElse("undefined");
			//@formatter:on
			log.error("Error notifying to Hyperwallet that all documents were sent: [{}]",
					HyperwalletLoggingErrorsUtil.stringify(e));
			kycMailNotificationUtil.sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
					"There was an error notifying Hyperwallet all documents were sent for shop Id [%s], so Hyperwallet will not be notified about this new situation%n%s",
					clientUserId, HyperwalletLoggingErrorsUtil.stringify(e)));
		}
	}

	private Optional<String> getHyperwalletProgram(final List<KYCDocumentInfoModel> documentInfoList) {
		//@formatter:off
		return CollectionUtils.emptyIfNull(documentInfoList)
				.stream()
				.filter(Objects::nonNull)
				.map(KYCDocumentInfoModel::getHyperwalletProgram)
				.filter(StringUtils::isNotEmpty)
				.findAny();
		//@formatter:off
	}

	private String getClientId(final List<KYCDocumentInfoModel> documentInfoList) {
		//@formatter:off
		return CollectionUtils.emptyIfNull(documentInfoList)
				.stream()
				.filter(Objects::nonNull)
				.map(KYCDocumentInfoModel::getClientUserId)
				.findAny()
				.orElse(null);
		//@formatter:on
	}

}
