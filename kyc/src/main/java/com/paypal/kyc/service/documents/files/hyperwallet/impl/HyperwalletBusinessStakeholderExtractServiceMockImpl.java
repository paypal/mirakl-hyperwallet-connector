package com.paypal.kyc.service.documents.files.hyperwallet.impl;

import com.google.gson.Gson;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletUser;
import com.hyperwallet.clientsdk.model.HyperwalletVerificationDocument;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.HyperwalletLoggingErrorsUtil;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import com.paypal.kyc.model.KYCDocumentInfoModel;
import com.paypal.kyc.model.KYCDocumentModel;
import com.paypal.kyc.service.HyperwalletSDKService;
import com.paypal.kyc.strategies.documents.files.hyperwallet.businessstakeholder.impl.KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that mocks behaviour of sending business stakeholder KYC documents to mockserver
 * instead of sending them to HW
 */

@Profile({ "qa" })
@Slf4j
@Service("hyperwalletBusinessStakeholderExtractService")
public class HyperwalletBusinessStakeholderExtractServiceMockImpl
		extends HyperwalletBusinessStakeholderExtractServiceImpl {

	private static final String FAILING_FILES = "fail";

	private final String mockServerUrl;

	private final RestTemplate restTemplate;

	private static final String HYPERWALLET_PUSH_DOCUMENTS = "/hyperwallet/v4/{userToken}/{bstToken}/documents";

	private static final String HYPERWALLET_NOTIFY_USER = "/hyperwallet/v4/{userToken}";

	public HyperwalletBusinessStakeholderExtractServiceMockImpl(final HyperwalletSDKService hyperwalletSDKService,
			final KYCBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyExecutor kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory,
			final MailNotificationUtil kycMailNotificationUtil, @Value("${mockserver.url}") final String mockServerUrl,
			final RestTemplate restTemplate) {
		super(hyperwalletSDKService,
				kycBusinessStakeholderDocumentInfoModelToHWVerificationDocumentMultipleStrategyFactory,
				kycMailNotificationUtil);
		this.mockServerUrl = mockServerUrl;
		this.restTemplate = restTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected KYCDocumentBusinessStakeHolderInfoModel callHyperwalletAPI(
			final Map.Entry<KYCDocumentBusinessStakeHolderInfoModel, List<HyperwalletVerificationDocument>> entry) {
		final KYCDocumentBusinessStakeHolderInfoModel kycDocumentBusinessStakeHolderInfoModel = entry.getKey();
		final List<HyperwalletVerificationDocument> hyperwalletVerificationDocuments = entry.getValue();

		final String postURL = HYPERWALLET_PUSH_DOCUMENTS
				.replace("{userToken}", kycDocumentBusinessStakeHolderInfoModel.getUserToken())
				.replace("{bstToken}", kycDocumentBusinessStakeHolderInfoModel.getToken());
		try {
			if (checkFailingFiles(hyperwalletVerificationDocuments)) {
				throw new HyperwalletException("Something bad happened");
			}
			Gson gsonConverter = new Gson();
			restTemplate.postForObject(getMockServerUrl() + postURL, gsonConverter.toJson(entry.getValue()),
					Object.class);
			log.info("Pushed successfully to mockserver documents [{}] for shopId  [{}]",
					kycDocumentBusinessStakeHolderInfoModel.getIdentityDocuments().stream()
							.map(KYCDocumentModel::getDocumentFieldName).collect(Collectors.joining(",")),
					kycDocumentBusinessStakeHolderInfoModel.getClientUserId());

			return kycDocumentBusinessStakeHolderInfoModel.toBuilder().sentToHyperwallet(true).build();
		}
		catch (HyperwalletException ex) {
			log.error("Error uploading document to hyperwallet: [{}]", HyperwalletLoggingErrorsUtil.stringify(ex));
			getKycMailNotificationUtil().sendPlainTextEmail("Issue detected pushing documents into Hyperwallet",
					String.format(
							"Something went wrong pushing documents to Hyperwallet for shop Id [%s] and business stakeholder number [%s]%n%s",
							kycDocumentBusinessStakeHolderInfoModel.getClientUserId(),
							kycDocumentBusinessStakeHolderInfoModel.getBusinessStakeholderMiraklNumber(),
							HyperwalletLoggingErrorsUtil.stringify(ex)));

			return kycDocumentBusinessStakeHolderInfoModel;
		}

	}

	@Override
	public List<KYCDocumentBusinessStakeHolderInfoModel> notifyAllDocumentsSentForBstk(
			final List<KYCDocumentBusinessStakeHolderInfoModel> documentsTriedToBeSent) {
		//@formatter:off
        final Map<String, List<KYCDocumentBusinessStakeHolderInfoModel>> bstkGroupedByClientId = documentsTriedToBeSent
                .stream()
                .collect(Collectors.groupingBy(KYCDocumentInfoModel::getUserToken));
        //@formatter:on

		//@formatter:off
        final Map<String, List<KYCDocumentBusinessStakeHolderInfoModel>> userWithBstkToBeNotified = bstkGroupedByClientId.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .allMatch(KYCDocumentBusinessStakeHolderInfoModel::isSentToHyperwallet))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        //@formatter:on

		//@formatter:off
        return userWithBstkToBeNotified.entrySet()
                .stream()
                .map(this::notifyBstk)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //@formatter:on

	}

	@Override
	protected List<KYCDocumentBusinessStakeHolderInfoModel> notifyBstk(
			final Map.Entry<String, List<KYCDocumentBusinessStakeHolderInfoModel>> entry) {
		final String token = entry.getKey();
		final HyperwalletUser user = new HyperwalletUser();
		user.setToken(token);
		user.setBusinessStakeholderVerificationStatus(
				HyperwalletUser.BusinessStakeholderVerificationStatus.READY_FOR_REVIEW);

		final String postURL = HYPERWALLET_NOTIFY_USER.replace("{userToken}", token);
		try {
			Gson gsonConverter = new Gson();
			restTemplate.put(getMockServerUrl() + postURL, gsonConverter.toJson(user), Object.class);
			log.info("Pushed successfully to mockserver business stakeholder notification update shopId  [{}]",
					entry.getValue().stream().map(KYCDocumentInfoModel::getClientUserId).findAny());

			return entry.getValue();
		}
		catch (HyperwalletException e) {
			final String clientUserId = Optional.ofNullable(entry.getValue().get(0))
					.orElse(KYCDocumentBusinessStakeHolderInfoModel.builder().build()).getClientUserId();

			log.error("Error notifying to Hyperwallet that all documents were sent: [{}]",
					HyperwalletLoggingErrorsUtil.stringify(e));
			getKycMailNotificationUtil().sendPlainTextEmail("Issue in Hyperwallet status notification", String.format(
					"There was an error notifying Hyperwallet all documents were sent for shop Id [%s], so Hyperwallet will not be notified about this new situation%n%s",
					clientUserId, HyperwalletLoggingErrorsUtil.stringify(e)));
			return entry.getValue();
		}
	}

	private boolean checkFailingFiles(final List<HyperwalletVerificationDocument> originalFiles) {
		//@formatter:off
        return Optional.ofNullable(originalFiles).orElse(List.of()).stream()
                .map(HyperwalletVerificationDocument::getUploadFiles)
                .filter(Objects::nonNull)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(fileName -> fileName.contains(FAILING_FILES));


        //@formatter:on
	}

	protected String getMockServerUrl() {
		return mockServerUrl;
	}

}
